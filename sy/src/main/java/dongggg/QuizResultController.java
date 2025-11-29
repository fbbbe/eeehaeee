package dongggg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dongggg.ConceptPair;
import dongggg.QuizService;
import dongggg.QuizServiceImpl;
import dongggg.DonggriRepository;

public class QuizResultController {

    @FXML
    private Label scorePercentLabel;
    @FXML
    private Label scoreSummaryLabel;
    @FXML
    private ProgressBar scoreBar;
    @FXML
    private Button com;

    @FXML
    private VBox resultListBox;

    private Parent previousRoot;
    private List<ConceptPair> quizListRef;
    private List<Boolean> answerStateList;
    private final QuizService quizService = new QuizServiceImpl();
    private boolean resultsPersisted = false;

    @FXML
    private void initialize() {
        if (com != null) {
            HoverEffects.installYellowHover(com);
        }
        // ⚡ 퀴즈 결과 화면에서도 전역 styles.css가 항상 적용되도록 보장
        scorePercentLabel.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                String css = App.class.getResource("styles.css").toExternalForm();
                if (!newScene.getStylesheets().contains(css)) {
                    newScene.getStylesheets().add(css);
                }
            }
        });
    }

    public void showResult(List<ConceptPair> quizList, List<String> userAnswers) {

        this.quizListRef = quizList;
        int total = quizList.size();
        answerStateList = new ArrayList<>(Collections.nCopies(total, false));

        resultListBox.getChildren().clear();

        for (int i = 0; i < total; i++) {

            ConceptPair pair = quizList.get(i);

            String correct = pair.getExplanation();
            String user = userAnswers.get(i);

            boolean isCorrect = user.equalsIgnoreCase(correct);
            answerStateList.set(i, isCorrect);

            resultListBox.getChildren().add(
                    createResultCard(pair.getTerm(), correct, user, isCorrect, i));
        }

        updateTotalScoreUI();
    }

    private VBox createResultCard(String concept, String correct, String user,
            boolean isCorrect, int index) {

        VBox card = new VBox(12);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-padding: 22;" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-color: #e8dff5;" +
                        "-fx-border-radius: 22;" +
                        "-fx-border-width: 2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(168,85,221,0.10), 20, 0.2, 0, 4);");

        Label conceptLabel = new Label("개념: " + concept);
        conceptLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #7c3aed;");

        Label correctLabel = new Label("정답: " + correct);
        correctLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #b79bff;");

        VBox userBox = new VBox(4);
        userBox.setStyle(
                "-fx-background-color: #f6f1ff;" +
                        "-fx-padding: 12;" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: #e8dff5;" +
                        "-fx-border-radius: 14;");

        Label userTitle = new Label("당신의 답변");
        Label userValue = new Label(user.isEmpty() ? "(답변 없음)" : user);

        userTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #c9b4ff;");
        userValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #3b275b; -fx-font-weight: 600;");

        userBox.getChildren().addAll(userTitle, userValue);

        Button wrongBtn = new Button("✖ 틀림");
        Button correctBtn = new Button("✔ 맞음");

        wrongBtn.setStyle(buttonStyle(false));
        correctBtn.setStyle(buttonStyle(false));

        updateButtonState(wrongBtn, correctBtn, isCorrect);

        wrongBtn.setOnAction(e -> {
            answerStateList.set(index, false);
            updateButtonState(wrongBtn, correctBtn, false);
            updateTotalScoreUI();
        });

        correctBtn.setOnAction(e -> {
            answerStateList.set(index, true);
            updateButtonState(wrongBtn, correctBtn, true);
            updateTotalScoreUI();
        });

        HBox buttonRow = new HBox(12, wrongBtn, correctBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(conceptLabel, correctLabel, userBox, buttonRow);

        return card;
    }

    private String buttonStyle(boolean active) {
        if (active) {
            return "-fx-background-color: #eedcff;" +
                    "-fx-text-fill: #5b29cc;" +
                    "-fx-font-weight: 700;" +
                    "-fx-padding: 10 22;" +
                    "-fx-background-radius: 14;";
        } else {
            return "-fx-background-color: #f0eaff;" +
                    "-fx-text-fill: #7d5fd9;" +
                    "-fx-font-weight: 600;" +
                    "-fx-padding: 10 22;" +
                    "-fx-background-radius: 14;" +
                    "-fx-opacity: 0.55;";
        }
    }

    private void updateButtonState(Button wrongBtn, Button correctBtn, boolean isCorrect) {
        if (isCorrect) {
            correctBtn.setStyle(buttonStyle(true));
            wrongBtn.setStyle(buttonStyle(false));
        } else {
            wrongBtn.setStyle(buttonStyle(true));
            correctBtn.setStyle(buttonStyle(false));
        }
    }

    private void updateTotalScoreUI() {
        int total = answerStateList.size();
        int correctCount = 0;
        for (boolean b : answerStateList)
            if (b)
                correctCount++;

        int scorePercent = (int) Math.round((correctCount * 100.0) / total);

        scorePercentLabel.setText(scorePercent + "%");
        scoreSummaryLabel.setText(correctCount + " / " + total + "개 정답");
        scoreBar.setProgress(scorePercent / 100.0);
    }

    private void persistResults(List<ConceptPair> quizList) {
        if (resultsPersisted || quizList == null || quizList.isEmpty())
            return;

        int correctCount = 0;
        for (Boolean b : answerStateList) {
            if (Boolean.TRUE.equals(b))
                correctCount++;
        }
        int total = answerStateList.size();
        int scorePercent = total == 0 ? 0 : (int) Math.round(correctCount * 100.0 / total);

        for (int i = 0; i < quizList.size(); i++) {
            boolean isCorrect = Boolean.TRUE.equals(answerStateList.get(i));
            quizService.updateResult(quizList.get(i), isCorrect);
        }

        // 누적 점수는 이번 시험 점수를 그대로 더하고, 맞춘 개수를 누적
        DonggriRepository.addProgress(scorePercent, correctCount);

        resultsPersisted = true;
    }

    @FXML
    private void goBack() {
        persistResults(quizListRef);
        if (previousRoot != null) {
            App.swapRootKeepingState(previousRoot);
        }
    }

    @FXML
    private void goDashboard() {

        persistResults(quizListRef);

        // 🔥 최근 시험 결과 저장
        int correctCount = getCorrectCount();
        int totalQuestions = answerStateList.size();
        DonggriRepository.setLastExamResult(correctCount, totalQuestions);

        DonggriRepository.addExamCount();
        App.showDashboardView();
    }

    public void setPreviousRoot(Parent root) {
        this.previousRoot = root;
    }

    public int getCorrectCount() {
        int correctCount = 0;
        for (boolean b : answerStateList) {
            if (b)
                correctCount++;
        }
        return correctCount;
    }
}

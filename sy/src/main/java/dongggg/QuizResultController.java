package dongggg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizResultController {

    // 점수 요약 영역
    @FXML private Label scorePercentLabel;
    @FXML private Label scoreSummaryLabel;
    @FXML private ProgressBar scoreBar;

    // 문제별 카드 컨테이너
    @FXML private VBox resultListBox;

    // 뒤로가기용
    private Scene previousScene;

    // 🔥 문제별 정답 상태 저장 (DB 연결 대비)
    private List<Boolean> answerStateList;

    /** 🔥 시험 결과 표시 */
    public void showResult(List<ConceptPair> quizList, List<String> userAnswers) {

        int total = quizList.size();
        int correctCount = 0;

        // 문제 정답상태 리스트 초기화
        answerStateList = new ArrayList<>(Collections.nCopies(total, false));

        // UI 초기화
        resultListBox.getChildren().clear();

        for (int i = 0; i < total; i++) {
            ConceptPair pair = quizList.get(i);
            String correct = pair.getExplanation();
            String user = userAnswers.get(i);

            boolean isCorrect = user.equalsIgnoreCase(correct);
            if (isCorrect) correctCount++;

            answerStateList.set(i, isCorrect);

            // UI 카드 추가
            resultListBox.getChildren().add(
                    createResultCard(pair.getTerm(), correct, user, isCorrect, i)
            );
        }

        // 총점 표시
        updateTotalScoreUI();
    }

    /** 🔥 문제별 카드 생성 + 정답/오답 버튼 동작 포함 */
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
            "-fx-effect: dropshadow(gaussian, rgba(168,85,221,0.10), 20, 0.2, 0, 4);"
        );

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
            "-fx-border-radius: 14;"
        );

        Label userTitle = new Label("당신의 답변");
        userTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #c9b4ff;");

        Label userValue = new Label(user.isEmpty() ? "(답변 없음)" : user);
        userValue.setStyle("-fx-font-size: 14px; -fx-text-fill: #3b275b; -fx-font-weight: 600;");

        userBox.getChildren().addAll(userTitle, userValue);

        // ----------------------------
        // 🔥 정답/오답 버튼
        // ----------------------------
        Button wrongBtn = new Button("✖ 틀림");
        Button correctBtn = new Button("✔ 맞음");

        wrongBtn.setStyle(buttonStyle(false));
        correctBtn.setStyle(buttonStyle(false));

        // 기존 정답 여부에 따라 스타일 설정
        updateButtonState(wrongBtn, correctBtn, isCorrect);

        // 버튼 동작
        wrongBtn.setOnAction(e -> {
            answerStateList.set(index, false);
            updateButtonState(wrongBtn, correctBtn, false);
            updateTotalScoreUI();  // 🔥 상단 점수 즉시 갱신
        });

        correctBtn.setOnAction(e -> {
            answerStateList.set(index, true);
            updateButtonState(wrongBtn, correctBtn, true);
            updateTotalScoreUI();  // 🔥 상단 점수 즉시 갱신
        });

        HBox buttonRow = new HBox(12, wrongBtn, correctBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(conceptLabel, correctLabel, userBox, buttonRow);

        return card;
    }

    /** 🔥 스타일 생성 함수 */
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

    /** 🔥 정답/오답 버튼 클릭 시 시각적 상태 변경 */
    private void updateButtonState(Button wrongBtn, Button correctBtn, boolean isCorrect) {
        if (isCorrect) {
            correctBtn.setStyle(buttonStyle(true));
            wrongBtn.setStyle(buttonStyle(false));
        } else {
            wrongBtn.setStyle(buttonStyle(true));
            correctBtn.setStyle(buttonStyle(false));
        }
    }

    /** 🔥 전체 점수 다시 계산하여 상단 UI 업데이트 */
    private void updateTotalScoreUI() {
        int total = answerStateList.size();
        int correctCount = 0;

        for (boolean state : answerStateList) {
            if (state) correctCount++;
        }

        int scorePercent = (int) Math.round((correctCount * 100.0) / total);

        scorePercentLabel.setText(scorePercent + "%");
        scoreSummaryLabel.setText(correctCount + " / " + total + "개 정답");
        scoreBar.setProgress(scorePercent / 100.0);
    }

    /** 🔙 뒤로가기 */
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) resultListBox.getScene().getWindow();
            if (previousScene != null) {
                stage.setScene(previousScene);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 🔥 대시보드로 이동 */
    @FXML
    private void goDashboard() {
        System.out.println("[QuizResult] goDashboard called!");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();

            Scene dashboardScene = new Scene(root, 1200, 720);
            dashboardScene.getStylesheets().add(
                    getClass().getResource("styles.css").toExternalForm()
            );

            Stage stage = (Stage) resultListBox.getScene().getWindow();
            stage.setScene(dashboardScene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 🔙 이전 화면 저장 */
    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
}

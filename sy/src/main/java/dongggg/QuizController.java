package dongggg;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;
import java.util.ArrayList;
import dongggg.Note;
import dongggg.MascotProvider;
import dongggg.DonggriRepository;
import dongggg.QuizService;

public class QuizController {

    @FXML
    private Label conceptLabel;
    @FXML
    private TextArea answerArea;
    @FXML
    private Label progressLabel;
    @FXML
    private Label progressTopLabel;
    @FXML
    private Label progressPercentLabel;
    @FXML
    private ProgressBar questionProgressBar;
    @FXML
    private Label timerLabel; // ⬅️ 타이머 표시 Label (FXML에 있어야 함!)
    @FXML
    private ImageView quizMascotImage;
    @FXML
    private Button next;

    private final QuizService quizService = new QuizServiceImpl();
    private List<ConceptPair> quizList = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();

    private int currentIndex = 0;

    // 🔥 타이머 관련 필드
    private int elapsedTime = 0;
    private Timeline timer;

    private Parent previousRoot;

    public void initQuiz(List<Note> selectedNotes, QuizService.QuizMode mode, int limit) {
        if (next != null) {
            HoverEffects.installYellowHover(next);
        }
        List<Integer> noteIds = new ArrayList<>();
        if (selectedNotes != null) {
            for (Note note : selectedNotes) {
                if (note != null) {
                    noteIds.add(note.getId());
                }
            }
        }

        quizList = quizService.generateQuiz(noteIds, mode, limit);

        if (quizList == null || quizList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("문제 없음");
            alert.setHeaderText(null);
            alert.setContentText("선택한 노트에 저장된 개념/설명이 없습니다. 먼저 문제를 등록하세요.");
            alert.showAndWait();
            goBack();
            return;
        }

        currentIndex = 0;
        loadQuestion(currentIndex);

        // 🔥 타이머 시작
        startTimer();

        if (quizMascotImage != null) {
            quizMascotImage.setImage(MascotProvider.loadForLevel(DonggriRepository.getLevelInfo().getCurrentLevel()));
        }
    }

    private void loadQuestion(int index) {
        ConceptPair cp = quizList.get(index);
        conceptLabel.setText(cp.getTerm());
        updateProgress();
    }

    private void updateProgress() {
        int total = quizList.size();
        int current = currentIndex + 1;
        double ratio = total == 0 ? 0 : (double) current / total;

        String text = current + " / " + total + " 문제";
        if (progressLabel != null) {
            progressLabel.setText(text);
        }
        if (progressTopLabel != null) {
            progressTopLabel.setText("문제 " + current + " / " + total);
        }
        if (progressPercentLabel != null) {
            int percent = (int) Math.round(ratio * 100);
            progressPercentLabel.setText(percent + "%");
        }
        if (questionProgressBar != null) {
            questionProgressBar.setProgress(ratio);
        }
    }

    // 🔥 타이머 시작 함수
    private void startTimer() {
        timer = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    elapsedTime++;
                    updateTimerLabel();
                }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    // 🔥 타이머 표시 업데이트
    private void updateTimerLabel() {
        int minutes = elapsedTime / 60;
        int seconds = elapsedTime % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    // 🔥 타이머 정지
    private void stopTimer() {
        if (timer != null)
            timer.stop();
    }

    @FXML
    public void nextQuestion() {

        // 🔥 추가: 답변이 비어 있으면 넘어가지 못하도록 막기
        String answer = answerArea.getText().trim();
        if (answer.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("답변 필요");
            alert.setHeaderText(null);
            alert.setContentText("다음 문제로 넘어가기 전에 답을 입력하세요!");
            alert.showAndWait();
            return; // ❌ 그대로 머물기
        }

        // 🔥 기존 기능 유지: 답변 저장
        userAnswers.add(answer);
        answerArea.clear();

        currentIndex++;

        if (currentIndex >= quizList.size()) {
            stopTimer(); // 🔥 기존 타이머 종료 유지
            goToResult();
            return;
        }

        loadQuestion(currentIndex);
    }

    private void goToResult() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz-result-view.fxml"));
            Parent root = loader.load();

            QuizResultController controller = loader.getController();
            controller.showResult(quizList, userAnswers);

            // 🔥 정확한 정답 계산
            int correctCount = controller.getCorrectCount();
            int totalQuestions = quizList.size();

            // 🔥🔥🔥 추가: 최근 시험 결과 저장(정답률 계산용)
            DonggriRepository.setLastExamResult(correctCount, totalQuestions);

            // 🔥 기존 기능 유지: 이전 화면 저장
            controller.setPreviousRoot(App.getScene().getRoot());

            App.swapRootKeepingState(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPreviousRoot(Parent root) {
        this.previousRoot = root;
    }

    // 🔥 FXML용 goBack()
    @FXML
    private void goBack() {
        try {
            stopTimer(); // 타이머 정지

            if (previousRoot != null) {
                App.swapRootKeepingState(previousRoot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package dongggg;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.Duration;

import java.util.List;
import java.util.ArrayList;

public class QuizController {

    @FXML private Label conceptLabel;
    @FXML private TextArea answerArea;
    @FXML private Label progressLabel;
    @FXML private Label timerLabel;   // ⬅️ 타이머 표시 Label (FXML에 있어야 함!)

    private final QuizService quizService = new QuizServiceImpl();
    private List<ConceptPair> quizList = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();

    private int currentIndex = 0;

    // 🔥 타이머 관련 필드
    private int elapsedTime = 0;
    private Timeline timer;

    public void initQuiz(int noteId) {

        quizList = quizService.generateQuiz(noteId, 5);

        if (quizList == null || quizList.isEmpty()) {
            quizList = new ArrayList<>();
            quizList.add(new ConceptPair(0, noteId, "클래스(Class)", "객체를 만들기 위한 설계도", 0));
            quizList.add(new ConceptPair(0, noteId, "객체(Object)", "클래스로부터 생성된 실체", 0));
            quizList.add(new ConceptPair(0, noteId, "상속(Inheritance)", "부모 클래스 기능을 자식이 물려받는 것", 0));
        }

        currentIndex = 0;
        loadQuestion(currentIndex);

        // 🔥 타이머 시작
        startTimer();
    }

    private void loadQuestion(int index) {
        ConceptPair cp = quizList.get(index);
        conceptLabel.setText(cp.getTerm());
        updateProgress();
    }

    private void updateProgress() {
        progressLabel.setText((currentIndex + 1) + " / " + quizList.size() + " 문제");
    }

    // 🔥 타이머 시작 함수
    private void startTimer() {
        timer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                elapsedTime++;
                updateTimerLabel();
            })
        );
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
        if (timer != null) timer.stop();
    }

    @FXML
    public void nextQuestion() {
        userAnswers.add(answerArea.getText());
        answerArea.clear();

        currentIndex++;

        if (currentIndex >= quizList.size()) {
            stopTimer();   // 🔥 결과 보기 직전 타이머 종료
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

            Stage stage = (Stage) conceptLabel.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 FXML용 goBack()
    @FXML
    private void goBack() {
        try {
            stopTimer(); // 뒤로가기 시 타이머도 정지

            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) conceptLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

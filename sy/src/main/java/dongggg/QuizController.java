package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.util.List;
import java.util.ArrayList;

public class QuizController {

    @FXML private Label conceptLabel;
    @FXML private TextArea answerArea;
    @FXML private Label progressLabel;

    private final QuizService quizService = new QuizServiceImpl();
    private List<ConceptPair> quizList = new ArrayList<>();
    private List<String> userAnswers = new ArrayList<>();

    private int currentIndex = 0;

    public void initQuiz(int noteId) {

        // 🔥 문제 5개 생성!
        quizList = quizService.generateQuiz(noteId, 5);

        if (quizList == null || quizList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("문제 없음");
            alert.setHeaderText("선택한 노트에 문제가 없습니다.");
            alert.setContentText("개념을 최소 1개 이상 등록한 뒤 시험을 시작하세요.");
            alert.showAndWait();
            return;
        }

        currentIndex = 0;
        loadQuestion(currentIndex);
    }

    private void loadQuestion(int index) {
        ConceptPair cp = quizList.get(index);
        conceptLabel.setText(cp.getTerm());
        updateProgress();
    }

    private void updateProgress() {
        progressLabel.setText((currentIndex + 1) + " / " + quizList.size() + " 문제");
    }

    @FXML
    public void nextQuestion() {
        userAnswers.add(answerArea.getText());
        answerArea.clear();

        currentIndex++;

        if (currentIndex >= quizList.size()) {
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
}

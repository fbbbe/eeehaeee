// 시험 진행 제어

package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
        quizList = quizService.generateQuiz(noteId, 5);

        conceptLabel.setText(quizList.get(0).getTerm());  // 🔥 변경됨
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

        conceptLabel.setText(quizList.get(currentIndex).getTerm()); // 🔥 변경됨
        updateProgress();
    }

    private void goToResult() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz-result-view.fxml"));
            Parent root = loader.load();

            QuizResultController controller = loader.getController();
            controller.showResult(quizList, userAnswers);

            Stage stage = (Stage) conceptLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

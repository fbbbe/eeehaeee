package dongggg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;

public class QuizModeSelectController {

    @FXML private Label totalCountLabel;
    @FXML private Label totalTimeLabel;
    @FXML private Slider worstCountSlider;
    @FXML private Label worstCountValueLabel;
    @FXML private Label worstSelectedCountLabel;
    @FXML private Label worstTimeLabel;
    @FXML private Button btn5;
    @FXML private Button btn10;
    @FXML private Button btn15;
    @FXML private Button btn20;




    private List<Note> selectedNotes = new ArrayList<>();
    private int totalQuestions = 0;

    // QuizStart 화면의 Scene 저장
    private Scene previousScene;

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setSelectedNotes(List<Note> notes) {
        this.selectedNotes = (notes != null) ? notes : new ArrayList<>();
        updateCounts();
    }

    @FXML
    private void initialize() {
        worstCountSlider.valueProperty().addListener((obs, o, n) -> updateWorstCountDisplay());
        worstCountSlider.setMin(1);
        worstCountSlider.setMax(1.1);   // 문제 1개일 때도 동그라미 보이게

    }

    private void updateCounts() {
        List<Integer> ids = selectedNotes.stream().map(Note::getId).toList();
        totalQuestions = ConceptPairRepository.countByNoteIds(ids);

        totalCountLabel.setText(totalQuestions + "문제");
        totalTimeLabel.setText(estimateMinutes(totalQuestions) + "분");

        int max = Math.max(1, totalQuestions);
        int defaultWorst = Math.min(10, max);
        // 문제 1개일 때도 슬라이더 동그라미(thumb) 보이게 max 살짝 늘리기
        if (max == 1) {
            worstCountSlider.setMax(1.1);
        } else {
        worstCountSlider.setMax(max);
}

        worstCountSlider.setValue(defaultWorst);

        updateWorstCountDisplay();
    }

    private void updateWorstCountDisplay() {
        int count = (int) Math.round(worstCountSlider.getValue());
        worstCountValueLabel.setText(count + "");
        worstSelectedCountLabel.setText(count + "문제");
        worstTimeLabel.setText(estimateMinutes(count) + "분");
    }

    private int estimateMinutes(int questions) {
        return Math.max(1, (int) Math.round(questions * 1.5));
    }

    @FXML private void onQuickSelect5()  { quickSelect(5, btn5); }
    @FXML private void onQuickSelect10() { quickSelect(10, btn10); }
    @FXML private void onQuickSelect15() { quickSelect(15, btn15); }
    @FXML private void onQuickSelect20() { quickSelect(20, btn20); }

    private void quickSelect(int count, Button button) {
        int max = (int) worstCountSlider.getMax();
        worstCountSlider.setValue(Math.min(count, max));
        updateQuickSelectStyles(button);
    }


    @FXML
    private void onStartAll() {
        if (totalQuestions <= 0) return;
        startQuiz(QuizService.QuizMode.ALL, totalQuestions);
    }

    @FXML
    private void onStartWorst() {
        if (totalQuestions <= 0) return;
        int count = (int) worstCountSlider.getValue();
        startQuiz(QuizService.QuizMode.WORST, count);
    }

    // 뒤로가기 → QuizStart 화면으로 이동
    @FXML
    private void goBack() {
        try {
            Stage stage = (Stage) totalCountLabel.getScene().getWindow();
            if (previousScene != null) {
                stage.setScene(previousScene);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startQuiz(QuizService.QuizMode mode, int limit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz-view.fxml"));
            Parent root = loader.load();

            QuizController controller = loader.getController();
            controller.initQuiz(selectedNotes, mode, limit);
            controller.setPreviousScene(worstCountSlider.getScene());

            Stage stage = App.getStage();
            stage.setScene(new Scene(root, 1200, 720));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateQuickSelectStyles(Button activeButton) {
        Button[] buttons = {btn5, btn10, btn15, btn20};

        for (Button b : buttons) {
            b.getStyleClass().removeAll("quiz-pill-primary", "quiz-pill-outline");

            if (b == activeButton) b.getStyleClass().add("quiz-pill-primary");
            else b.getStyleClass().add("quiz-pill-outline");
        }
    }

}

package dongggg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuizStartController {

    @FXML
    private VBox noteListBox;
    @FXML
    private Label selectedCountLabel;
    @FXML
    private Button startButton;
    @FXML
    private Button cancel;

    private List<NoteCardController> cardControllers = new ArrayList<>();
    private final QuizService quizService = new QuizServiceImpl();

    @FXML
    public void initialize() {
        if (startButton != null) {
            HoverEffects.installYellowHover(startButton);
        }

        if (cancel != null) {
            HoverEffects.installPurpleHover(cancel);
        }

        List<Note> notes = NoteRepository.findByType("CONCEPT", 30);

        if (notes.isEmpty()) {
            Label empty = new Label("개념 노트가 없습니다. 개념 노트를 먼저 만들어주세요.");
            empty.getStyleClass().add("info-label");
            noteListBox.getChildren().add(empty);
            return;
        }

        for (Note note : notes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("note-card.fxml"));
                Parent card = loader.load();

                NoteCardController controller = loader.getController();
                controller.setData(note);
                controller.setMoreVisible(false);

                controller.getCheckBox().selectedProperty()
                        .addListener((o, oldV, newV) -> updateSelectedCount());

                cardControllers.add(controller);

                // 💜 퀴즈 시작 화면의 노트 카드에도 보라 hover 효과 적용
                if (card instanceof Region) {
                    HoverEffects.installPurpleHover((Region) card);
                }

                noteListBox.getChildren().add(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /** 선택된 노트 개수 업데이트 */
    private void updateSelectedCount() {
        long count = cardControllers.stream()
                .filter(NoteCardController::isSelected)
                .count();

        selectedCountLabel.setText(count + "개의 노트가 선택되었습니다");
        startButton.setDisable(count == 0);
    }

    /** 🔙 대시보드 이동 */
    @FXML
    private void goDashboard() {
        App.showDashboardView();
    }

    /** 뒤로가기 = 대시보드 */
    @FXML
    private void goBack() {
        App.showDashboardView();
    }

    /** 🔥 시험 시작 */
    @FXML
    public void startQuiz() {

        List<Note> selectedNotes = cardControllers.stream()
                .filter(NoteCardController::isSelected)
                .map(NoteCardController::getNote)
                .toList();

        if (selectedNotes.isEmpty())
            return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz-mode-select-view.fxml"));
            Parent root = loader.load();

            QuizModeSelectController controller = loader.getController();
            controller.setSelectedNotes(selectedNotes);
            controller.setPreviousRoot(App.getScene().getRoot());

            App.swapRootKeepingState(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

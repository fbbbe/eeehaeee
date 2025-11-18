package dongggg;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class QuizStartController {

    @FXML private VBox noteListBox;           // 카드들이 들어가는 VBox
    @FXML private Label selectedCountLabel;   // "n개 선택됨"
    @FXML private Button startButton;         // 시험 시작 버튼

    private List<NoteCardController> cardControllers = new ArrayList<>();

    private final QuizService quizService = new QuizServiceImpl();

    @FXML
    public void initialize() {

        // 최근 노트 30개 가져오기
        List<Note> notes = NoteRepository.findRecent(30);

        for (Note note : notes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("note-card.fxml"));
                Parent card = loader.load();

                NoteCardController controller = loader.getController();
                controller.setData(note);

                // 체크박스 이벤트 → 선택 개수 갱신
                controller.getCheckBox().selectedProperty().addListener((obs, oldV, newV) -> updateSelectedCount());

                cardControllers.add(controller);
                noteListBox.getChildren().add(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 선택 개수 업데이트
    private void updateSelectedCount() {
        long count = cardControllers.stream()
                .filter(NoteCardController::isSelected)
                .count();

        selectedCountLabel.setText(count + "개의 노트가 선택되었습니다");
        startButton.setDisable(count == 0);
    }

    // 뒤로 가기 (선택 사항)
    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) noteListBox.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 시험 시작 → QuizController로 선택된 Note 목록 전달
    @FXML
    public void startQuiz() {

        // 선택된 노트만 필터링
        List<Note> selectedNotes = cardControllers.stream()
                .filter(NoteCardController::isSelected)
                .map(NoteCardController::getNote)
                .toList();

        if (selectedNotes.isEmpty()) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz-view.fxml"));
            Parent root = loader.load();

            QuizController controller = loader.getController();

            // 📌 단일 노트만 지원한다면 첫 번째 노트만 넘겨주기
            controller.initQuiz(selectedNotes.get(0).getId());

            Stage stage = (Stage) noteListBox.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

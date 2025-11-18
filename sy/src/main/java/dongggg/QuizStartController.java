// 시험 시작 화면 제어

package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.StringConverter;


public class QuizStartController {

    @FXML private ComboBox<Note> noteSelectCombo;

    private final NoteRepository noteRepo = new NoteRepository();
    private final QuizService quizService = new QuizServiceImpl();

    @FXML
    public void initialize() {

        // 콤보박스 표시 문자열 변경
        noteSelectCombo.setConverter(new StringConverter<Note>() {
            @Override
            public String toString(Note note) {
                return (note == null) ? "" : note.getTitle();
            }

            @Override
            public Note fromString(String string) {
                return null;
            }
        });

        // 🔥 getAllNotes() 없음 → findRecent() 사용
        noteSelectCombo.getItems().addAll(NoteRepository.findRecent(30));
    }


    @FXML
    public void startQuiz() {
        Note selected = noteSelectCombo.getValue();

        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "시험 볼 노트를 선택하세요!").show();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("quiz-view.fxml"));
            Parent root = loader.load();

            QuizController controller = loader.getController();
            controller.initQuiz(selected.getId());

            Stage stage = (Stage) noteSelectCombo.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


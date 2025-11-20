package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteDetailController {

    @FXML
    private BorderPane root;

    @FXML
    private TextField titleField;

    @FXML
    private TextArea contentArea;

    @FXML
    private Label dateLabel;

    @FXML
    private Button deleteButton;

    // 현재 편집 중인 노트
    private Note note;

    // 새 노트인지 여부
    private boolean isNew = true;

    @FXML
    private void initialize() {
        if (note == null) {
            setNewNoteMode();
        }
    }

    // 외부에서 편집할 노트를 주입
    public void setNote(Note note) {
        this.note = note;

        if (note != null) {
            // 기존 노트 편집 모드
            isNew = false;

            titleField.setText(note.getTitle());
            contentArea.setText(note.getContent());

            String date = note.getUpdatedAt() != null ? note.getUpdatedAt() : note.getCreatedAt();
            dateLabel.setText(date != null ? date : "");
            deleteButton.setDisable(false);

        } else {
            // 새 노트 작성 모드
            setNewNoteMode();
        }
    }

    @FXML
    private void onSave() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        if (title.isEmpty()) {
            showAlert("제목을 입력해주세요.");
            return;
        }

        if (isNew) {
            // 새 노트 INSERT
            NoteRepository.insert(new Note(title, content));
        } else {
            if (note == null) {
                closeWindow();
                return;
            }
            // 기존 노트 UPDATE
            note.setTitle(title);
            note.setContent(content);
            NoteRepository.update(note);

            // 화면에 보이는 날짜도 업데이트
            dateLabel.setText(
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        closeWindow();
    }

    @FXML
    private void onCancel() {
        closeWindow();
    }

    @FXML
    private void onDelete() {
        // 새 노트이거나 note가 없으면 삭제할 게 없음
        if (isNew || note == null) {
            closeWindow();
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText("노트를 삭제할까요?");
        alert.setContentText("이 작업은 되돌릴 수 없습니다.");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                NoteRepository.delete(note.getId());
                closeWindow();
            }
        });
    }

    // 현재 창 닫기 대신, 메인 화면으로 돌아가기
    private void closeWindow() {
        // App이 메인 화면 FXML을 다시 로드하고 root를 교체해 준다.
        App.showMainView();
    }

    // 경고창 표시
    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("알림");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setNewNoteMode() {
        isNew = true;
        titleField.clear();
        contentArea.clear();
        dateLabel.setText("새 노트");
        deleteButton.setDisable(true); // 새 노트는 삭제 버튼 비활성화
    }
}

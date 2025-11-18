package dongggg;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * 메인 화면(폴더 + 최근 노트 목록)을 담당하는 컨트롤러.
 * 여기서는 새 창(Stage)을 만들지 않고,
 * App.showXXX 메서드를 써서 같은 창 안에서 화면을 전환한다.
 */
public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private Button newFolderButton;

    @FXML
    private Button newNoteButton;

    @FXML
    private GridPane folderGrid;

    @FXML
    private VBox recentNotesBox;

    @FXML
    public void initialize() {
        System.out.println("MainController initialize");
        loadRecentNotes();
    }

    /**
     * 최근 노트 목록을 DB에서 읽어서 화면에 뿌려주는 메서드.
     */
    private void loadRecentNotes() {
        recentNotesBox.getChildren().clear();

        List<Note> notes = NoteRepository.findRecent(10);
        for (Note note : notes) {
            HBox card = createNoteCard(note);
            recentNotesBox.getChildren().add(card);
        }
    }

    /**
     * 하나의 노트 카드를 HBox로 만들어주는 함수.
     * 노트 타입에 따라 "일반 노트 / 개념 노트" 태그를 붙이고,
     * 클릭 시 해당 편집 화면으로 페이지 전환한다.
     */
    private HBox createNoteCard(Note note) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(8);
        card.getStyleClass().add("note-card");

        VBox textBox = new VBox(4);

        Label titleLabel = new Label(note.getTitle());
        titleLabel.getStyleClass().add("note-title");

        String tagText = note.getType().equalsIgnoreCase("CONCEPT") ?
                "개념 노트" : "일반 노트";

        Label tagLabel = new Label(tagText);
        tagLabel.getStyleClass().add("note-tag");

        Label dateLabel = new Label(note.getUpdatedAt() != null ? note.getUpdatedAt() : "");
        dateLabel.getStyleClass().add("note-date");

        HBox meta = new HBox(8, tagLabel, dateLabel);

        textBox.getChildren().addAll(titleLabel, meta);
        card.getChildren().add(textBox);

        card.setOnMouseClicked(e -> {
            if (note.getType().equalsIgnoreCase("CONCEPT")) {
                App.showConceptNoteEditor(note);
            } else {
                App.showNoteEditor(note);
            }
        });

        return card;
    }

    @FXML
    private void onNewFolder() {
        System.out.println("새 폴더 클릭");
    }

    @FXML
    private void onNewNote() {
        App.showNoteTypeSelect();
    }

    // 🔥🔥 동그리 클릭 시 대시보드 이동
    @FXML
    private void onClickDonggriIcon(MouseEvent e) {
        App.showDashboardView();
    }
}

package dongggg;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

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

    private void loadRecentNotes() {
        recentNotesBox.getChildren().clear();

        List<Note> notes = NoteRepository.findRecent(10);
        for (Note note : notes) {
            HBox card = createNoteCard(note);
            recentNotesBox.getChildren().add(card);
        }
    }

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

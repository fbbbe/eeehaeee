package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class NoteCardController {

    @FXML private Label titleLabel;
    @FXML private Label dateLabel;
    @FXML private CheckBox checkBox;

    private Note note;

    public void setData(Note note) {
        this.note = note;
        titleLabel.setText(note.getTitle());
        dateLabel.setText(note.getCreatedAt());
    }

    public boolean isSelected() {
        return checkBox.isSelected();
    }

    public Note getNote() {
        return note;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}

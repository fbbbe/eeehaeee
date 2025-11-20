package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FolderCreateController {

    @FXML
    private TextField folderNameField;

    @FXML
    private Label statusLabel;

    @FXML
    private void onBack() {
        App.showMainView();
    }

    @FXML
    private void onCreate() {
        String name = folderNameField.getText() != null ? folderNameField.getText().trim() : "";

        if (name.isEmpty()) {
            showStatus("폴더 이름을 입력해주세요.");
            return;
        }

        Folder folder = new Folder(name);
        FolderRepository.insert(folder);
        App.showMainView();
    }

    private void showStatus(String msg) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
        }
        System.out.println("[FolderCreate] " + msg);
    }
}

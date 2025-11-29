package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Side;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import dongggg.FolderRepository;
import dongggg.Folder;
import dongggg.NoteRepository;
import dongggg.NoteFolderRepository;
import dongggg.App;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

public class NoteCardController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private CheckBox checkBox;
    @FXML
    private Button moreButton;

    private Note note;

    public void setData(Note note) {
        this.note = note;
        titleLabel.setText(note.getTitle());
        dateLabel.setText(note.getUpdatedAt() != null ? note.getUpdatedAt() : note.getCreatedAt());
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

    public void setMoreVisible(boolean visible) {
        if (moreButton != null) {
            moreButton.setVisible(visible);
            moreButton.setManaged(visible);
        }
    }

    @FXML
    private void onMore(MouseEvent event) {
        if (note == null)
            return;

        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("note-context-menu");

        CustomMenuItem moveItem = new CustomMenuItem(buildMenuRow("→", "이동", false));
        moveItem.setHideOnClick(false);
        moveItem.setOnAction(e -> {
            menu.hide();
            showFolderMenu();
        });

        CustomMenuItem deleteItem = new CustomMenuItem(buildDeleteRow());
        deleteItem.setOnAction(e -> {
            NoteRepository.delete(note.getId());
            App.showMainView();
        });

        menu.getItems().add(moveItem);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(deleteItem);

        menu.show(moreButton, Side.LEFT, 0, 0);
        event.consume();
    }

    private void showFolderMenu() {
        ContextMenu submenu = new ContextMenu();
        submenu.getStyleClass().add("note-folder-menu");
        var folders = FolderRepository.findAll();

        for (Folder folder : folders) {
            CustomMenuItem item = new CustomMenuItem(buildFolderRow(folder.getName()));
            item.setHideOnClick(true);
            item.setOnAction(e -> {
                NoteFolderRepository.setNoteFolder(note.getId(), folder.getId());
                App.showMainView();
            });
            submenu.getItems().add(item);
        }

        if (submenu.getItems().isEmpty()) {
            MenuItem empty = new MenuItem("폴더가 없습니다");
            empty.setDisable(true);
            submenu.getItems().add(empty);
        }

        submenu.show(moreButton, Side.LEFT, -6, 0);
    }

    private HBox buildMenuRow(String iconText, String labelText, boolean danger) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("note-menu-item");

        Label icon = new Label(iconText);
        icon.getStyleClass().add(danger ? "note-menu-icon-danger" : "note-menu-icon");

        Label label = new Label(labelText);
        label.getStyleClass().add(danger ? "note-menu-label-danger" : "note-menu-label");

        row.getChildren().addAll(icon, label);
        return row;
    }

    private SVGPath createTrashIcon() {
        SVGPath trash = new SVGPath();
        // 🔥 Lucide trash-2 그대로 풀어쓴 path (순서는 상관없지만 그대로 두면 헷갈릴 일 없음)
        trash.setContent(
                "M3 6h18 " + // 위 가로선
                        "M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6 " + // 몸통
                        "M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2 " + // 뚜껑+손잡이
                        "M10 11v6 " + // 왼쪽 내부 막대
                        "M14 11v6" // 오른쪽 내부 막대
        );

        trash.setStroke(Color.web("#9CA3AF")); // 상세 페이지랑 같은 회색 계열
        trash.setFill(Color.TRANSPARENT);
        trash.setStrokeWidth(1.8);
        trash.setStrokeLineCap(StrokeLineCap.ROUND);
        trash.setStrokeLineJoin(StrokeLineJoin.ROUND);
        trash.setScaleX(0.85);
        trash.setScaleY(0.85);

        return trash;
    }

    private HBox buildDeleteRow() {
        SVGPath trash = createTrashIcon(); // ✅ 공용 아이콘 사용

        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().addAll("note-menu-item", "note-menu-delete");

        Label label = new Label("삭제");
        label.getStyleClass().add("note-menu-label-danger");

        row.getChildren().addAll(trash, label);
        return row;
    }

    private HBox buildFolderRow(String name) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("note-folder-item");

        SVGPath icon = new SVGPath();
        icon.setContent(
                "M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z");
        icon.setStroke(Color.web("#F4B400"));
        icon.setFill(Color.TRANSPARENT);
        icon.setStrokeWidth(1.6);
        icon.setScaleX(0.8);
        icon.setScaleY(0.8);

        Label label = new Label(name);
        label.getStyleClass().add("note-folder-label");

        row.getChildren().addAll(icon, label);
        return row;
    }
}

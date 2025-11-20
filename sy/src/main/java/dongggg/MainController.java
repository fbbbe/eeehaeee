package dongggg;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.util.List;
import dongggg.Folder;
import dongggg.FolderRepository;
import dongggg.NoteFolderRepository;
import dongggg.NoteRepository;

/**
 * 메인 화면(폴더 + 최근 노트 목록)을 담당하는 컨트롤러.
 * 여기서는 새 창(Stage)을 만들지 않고,
 * App.showXXX 메서드를 써서 같은 창 안에서 화면을 전환한다.
 */
public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private Button newNoteButton;

    @FXML
    private HBox folderRow;

    @FXML
    private VBox recentNotesBox;

    private static final Duration HOVER_DURATION = Duration.millis(240);
    private static final String FOLDER_ICON_COLOR = "#F4B400";

    @FXML
    public void initialize() {
        loadFolders();
        loadRecentNotes();
        applyFolderHoverAnimations();

        // 🔥🔍 검색 기능 추가
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            onSearch(newValue);
        });
    }

    /** 🔍 검색 기능 */
    private void onSearch(String keyword) {
        String k = keyword.trim();

        folderRow.getChildren().clear();
        recentNotesBox.getChildren().clear();

        if (k.isEmpty()) {
            loadFolders();
            loadRecentNotes();
            return;
        }

        // 폴더 검색
        List<Folder> fList = FolderRepository.search(k);
        for (Folder f : fList) {
            folderRow.getChildren().add(createFolderCard(f.getName(), 0));
        }

        // 노트 검색
        List<Note> nList = NoteRepository.search(k);
        for (Note n : nList) {
            recentNotesBox.getChildren().add(createNoteCard(n));
        }
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

        String tagText = note.getType().equalsIgnoreCase("CONCEPT") ? "개념 노트" : "일반 노트";

        Label tagLabel = new Label(tagText);
        tagLabel.getStyleClass().add("note-tag");

        Label dateLabel = new Label(note.getUpdatedAt() != null ? note.getUpdatedAt() : "");
        dateLabel.getStyleClass().add("note-date");

        HBox meta = new HBox(8, tagLabel, dateLabel);

        textBox.getChildren().addAll(titleLabel, meta);
        card.getChildren().add(textBox);

        HBox spacer = new HBox();
        spacer.setPrefWidth(10);
        spacer.setMinWidth(10);
        spacer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        card.getChildren().add(spacer);

        Button moreBtn = new Button("⋮");
        moreBtn.getStyleClass().add("note-more-button");
        moreBtn.setOnAction(e -> showNoteMenu(note, moreBtn));
        card.getChildren().add(moreBtn);

        card.setOnMouseClicked(e -> {
            if (note.getType().equalsIgnoreCase("CONCEPT")) {
                App.showConceptNoteEditor(note);
            } else {
                App.showNoteEditor(note);
            }
        });

        installHoverAnimation(
                card,
                Color.web("#ffffff", 0.98),
                Color.web("#f5f0fb"),
                Color.web("#e8dff5"),
                Color.web("#a855dd"),
                6, 18,
                0.05, 0.22);

        return card;
    }

    private void showNoteMenu(Note note, Region anchor) {
        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().add("note-context-menu");

        CustomMenuItem moveItem = new CustomMenuItem(buildMenuRow("→", "이동", false));
        moveItem.setHideOnClick(false);
        moveItem.setOnAction(e -> {
            menu.hide();
            showFolderSubmenu(note, anchor);
        });

        CustomMenuItem deleteItem = new CustomMenuItem(buildDeleteRow());
        deleteItem.setOnAction(e -> {
            NoteRepository.delete(note.getId());
            loadRecentNotes();
            loadFolders();
        });

        menu.getItems().add(moveItem);
        menu.getItems().add(new SeparatorMenuItem());
        menu.getItems().add(deleteItem);

        menu.show(anchor, Side.LEFT, 0, 0);
    }

    private void showFolderSubmenu(Note note, Region anchor) {
        ContextMenu submenu = new ContextMenu();
        submenu.getStyleClass().add("note-folder-menu");
        var folders = FolderRepository.findAll();

        for (Folder folder : folders) {
            CustomMenuItem item = new CustomMenuItem(buildFolderRow(folder.getName()));
            item.setHideOnClick(true);
            item.setOnAction(e -> {
                NoteFolderRepository.setNoteFolder(note.getId(), folder.getId());
                loadRecentNotes();
                loadFolders();
            });
            submenu.getItems().add(item);
        }

        if (submenu.getItems().isEmpty()) {
            MenuItem empty = new MenuItem("폴더가 없습니다");
            empty.setDisable(true);
            submenu.getItems().add(empty);
        }

        submenu.show(anchor, Side.LEFT, -6, 0);
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

    private HBox buildDeleteRow() {
        SVGPath trash = new SVGPath();
        trash.setContent("M3 6h18 M8 6v14a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V6 M10 6V4a2 2 0 0 1 2-2h0a2 2 0 0 1 2 2v2 M12 10v6 M16 10v6 M8 6h8");
        trash.setStroke(Color.web("#9CA3AF"));
        trash.setFill(Color.TRANSPARENT);
        trash.setStrokeWidth(1.8);
        trash.setScaleX(0.8);
        trash.setScaleY(0.8);

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
        icon.setContent("M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z");
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

    /** 저장된 폴더를 카드로 표시 */
    private void loadFolders() {
        if (folderRow == null) {
            return;
        }

        folderRow.getChildren().clear();

        var folderCounts = NoteFolderRepository.getFolderNoteCounts();

        // 기본 폴더 3종 (전체/개념/일반)
        NoteRepository.NoteStats stats = NoteRepository.getNoteStats();
        folderRow.getChildren().add(createFolderCard("전체 노트", stats.totalCount()));
        folderRow.getChildren().add(createFolderCard("개념 노트", stats.conceptCount()));
        folderRow.getChildren().add(createFolderCard("일반 노트", stats.normalCount()));

        // 사용자 생성 폴더
        var folders = FolderRepository.findAll();
        for (Folder folder : folders) {
            int count = folderCounts.getOrDefault(folder.getId(), 0);
            folderRow.getChildren().add(createFolderCard(folder.getName(), count));
        }
    }

    private HBox createFolderCard(String title, int count) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("folder-card");

        StackPane iconHolder = new StackPane();
        iconHolder.getStyleClass().add("folder-icon-wrapper");
        iconHolder.setPrefSize(40, 32);

        SVGPath icon = new SVGPath();
        icon.setContent(
                "M20 20a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2h-7.9a2 2 0 0 1-1.69-.9L9.6 3.9A2 2 0 0 0 7.93 3H4a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2Z");
        icon.setStroke(Color.web(FOLDER_ICON_COLOR));
        icon.setFill(Color.TRANSPARENT);
        icon.setStrokeWidth(1.8);
        icon.setScaleX(0.85);
        icon.setScaleY(0.85);

        iconHolder.getChildren().add(icon);

        VBox labels = new VBox(4);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("folder-card-title");
        Label countLabel = new Label(count + "개");
        countLabel.getStyleClass().add("folder-card-count");
        labels.getChildren().addAll(titleLabel, countLabel);

        card.getChildren().addAll(iconHolder, labels);
        return card;
    }

    private void applyFolderHoverAnimations() {
        if (folderRow == null) {
            return;
        }
        folderRow.getChildren().stream()
                .filter(node -> node instanceof Region)
                .map(node -> (Region) node)
                .forEach(region -> installHoverAnimation(
                        region,
                        Color.web("#ffffff"),
                        Color.web("#fff9e6"),
                        Color.web("#e8dff5"),
                        Color.web("#ffb547"),
                        6, 18,
                        0.05, 0.2));
    }

    @FXML
    private void onNewNote() {
        App.showNoteTypeSelect();
    }

    @FXML
    private void onNewFolder() {
        App.showFolderCreateView();
    }

    // 🔥🔥 동그리 클릭 또는 단축 버튼 클릭 시 대시보드 이동
    @FXML
    private void onOpenMascot() {
        App.showDashboardView();
    }

    private void installHoverAnimation(Region region,
            Color baseBackground,
            Color hoverBackground,
            Color baseBorder,
            Color hoverBorder,
            double baseShadowRadius,
            double hoverShadowRadius,
            double baseShadowOpacity,
            double hoverShadowOpacity) {
        if (region == null) {
            return;
        }

        final String baseStyle = region.getStyle() == null ? "" : region.getStyle();
        DoubleProperty progress = new SimpleDoubleProperty(0);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(2);
        shadow.setRadius(baseShadowRadius);
        shadow.setColor(Color.rgb(168, 85, 221, baseShadowOpacity));
        region.setEffect(shadow);

        region.setStyle(baseStyle
                + "-fx-background-color: " + toCss(baseBackground) + ";"
                + "-fx-border-color: " + toCss(baseBorder) + ";");

        progress.addListener((obs, oldVal, newVal) -> {
            double t = newVal.doubleValue();
            Color bg = baseBackground.interpolate(hoverBackground, t);
            Color border = baseBorder.interpolate(hoverBorder, t);

            region.setStyle(baseStyle
                    + "-fx-background-color: " + toCss(bg) + ";"
                    + "-fx-border-color: " + toCss(border) + ";");

            double radius = baseShadowRadius + (hoverShadowRadius - baseShadowRadius) * t;
            double opacity = baseShadowOpacity + (hoverShadowOpacity - baseShadowOpacity) * t;

            shadow.setRadius(radius);
            shadow.setColor(Color.rgb(168, 85, 221, opacity));
        });

        Timeline hoverTimeline = new Timeline(
                new KeyFrame(HOVER_DURATION, new KeyValue(progress, 1, Interpolator.EASE_BOTH)));
        Timeline exitTimeline = new Timeline(
                new KeyFrame(HOVER_DURATION, new KeyValue(progress, 0, Interpolator.EASE_BOTH)));

        region.hoverProperty().addListener((obs, wasHover, isHover) -> {
            if (isHover) {
                exitTimeline.stop();
                hoverTimeline.playFromStart();
            } else {
                hoverTimeline.stop();
                exitTimeline.playFromStart();
            }
        });
    }

    private String toCss(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        double opacity = Math.round(color.getOpacity() * 1000) / 1000.0;
        return String.format("rgba(%d,%d,%d,%.3f)", r, g, b, opacity);
    }
}

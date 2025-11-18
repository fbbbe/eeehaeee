package dongggg;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
    private FlowPane folderFlow;

    @FXML
    private VBox recentNotesBox;

    private static final Duration HOVER_DURATION = Duration.millis(240);

    @FXML
    public void initialize() {
        System.out.println("MainController initialize");
        loadRecentNotes();
        applyFolderHoverAnimations();
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

        installHoverAnimation(
                card,
                Color.web("#ffffff", 0.92),
                Color.web("#f4ebff"),
                Color.web("#e2d5ff"),
                Color.web("#c0a9ff"),
                4, 14,
                0.02, 0.14
        );

        return card;
    }

    private void applyFolderHoverAnimations() {
        if (folderFlow == null) {
            return;
        }
        folderFlow.getChildren().stream()
                .filter(node -> node instanceof Region)
                .map(node -> (Region) node)
                .forEach(region -> installHoverAnimation(
                        region,
                        Color.web("#fefbff"),
                        Color.web("#f1e5ff"),
                        Color.web("#e7cffc"),
                        Color.web("#c498ff"),
                        6, 18,
                        0.02, 0.16
                ));
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
        shadow.setColor(Color.rgb(124, 58, 237, baseShadowOpacity));
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
            shadow.setColor(Color.rgb(124, 58, 237, opacity));
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

package dongggg;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Button noteManageButton;
    @FXML private Button newNoteButton;
    @FXML private Button quizButton;
    @FXML private Label levelValueLabel;
    @FXML private Label levelHelperLabel;
    @FXML private ProgressBar levelProgressBar;
    @FXML private Label conceptNoteCountLabel;


    private static final Duration HOVER_DURATION = Duration.millis(220);

    @FXML
    private void initialize() {
        installHoverAnimation(
                noteManageButton,
                Color.web("#a855dd"), Color.web("#9333cc"),
                Color.web("#b46af0"), Color.web("#a855dd"),
                Color.TRANSPARENT,
                Color.TRANSPARENT
        );
        installHoverAnimation(
                newNoteButton,
                Color.web("#ffe999"), Color.web("#ffd966"),
                Color.web("#ffd966"), Color.web("#ffc640"),
                Color.TRANSPARENT,
                Color.TRANSPARENT
        );
        installHoverAnimation(
                quizButton,
                Color.web("#ffffff"), Color.web("#ffffff"),
                Color.web("#f5f0fb"), Color.web("#ffffff"),
                Color.web("#e8dff5"),
                Color.web("#a855dd")
        );

        updateLevelCard();
        updateConceptNoteCount();
    }

    /** 🔥 개념 노트 개수 갱신 */
    private void updateConceptNoteCount() {
        int count = NoteRepository.getConceptNoteCount();

        if (conceptNoteCountLabel != null) {
            conceptNoteCountLabel.setText(String.valueOf(count));
        }
    }

    /** 🔥 노트 관리 화면 이동 — Scene 방식 */
    @FXML
    private void goNoteManager() {
        switchScene("main-view.fxml");
    }

    /** 🔥 새 노트 작성 화면 이동 — Scene 방식 */
    @FXML
    private void goConceptNote() {
        switchScene("note-type-select-view.fxml");
    }

    /** 🔥 시험 시작 화면 이동 — Scene 방식 */
    @FXML
    private void goQuiz() {
        switchScene("quiz-start-view.fxml");
    }


    /** 중앙 공용: Scene 교체 함수 */
    private void switchScene(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = (Stage) noteManageButton.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 720);

            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLevelCard() {
        DonggriLevelInfo info = DonggriRepository.getLevelInfo();

        if (levelValueLabel != null) {
            levelValueLabel.setText("Lv. " + info.getCurrentLevel());
        }

        if (levelHelperLabel != null) {
            if (info.isMaxLevel()) {
                levelHelperLabel.setText("최고 레벨입니다!");
            } else {
                levelHelperLabel.setText(String.format("다음 레벨까지 %d점 / %d문제",
                        info.getRemainingScore(),
                        info.getRemainingCorrect()));
            }
        }

        if (levelProgressBar != null) {
            levelProgressBar.setProgress(info.getProgressRatio());
        }
    }

    // ===== 아래는 hover animation 그대로 유지 =====

    private void installHoverAnimation(Button button,
                                       Color baseBgStart, Color baseBgEnd,
                                       Color hoverBgStart, Color hoverBgEnd,
                                       Color baseBorder, Color hoverBorder) {
        if (button == null) return;

        final String baseStyle = button.getStyle() == null ? "" : button.getStyle();
        DoubleProperty progress = new SimpleDoubleProperty(0);

        progress.addListener((obs, oldVal, newVal) -> {
            double t = newVal.doubleValue();
            Color bg1 = baseBgStart.interpolate(hoverBgStart, t);
            Color bg2 = baseBgEnd.interpolate(hoverBgEnd, t);
            Color border = baseBorder.interpolate(hoverBorder, t);
            String background = toLinearGradient(bg1, bg2);

            button.setStyle(baseStyle
                    + "-fx-background-color: " + background + ";"
                    + "-fx-border-color: " + toCss(border) + ";");
        });

        button.setStyle(baseStyle
                + "-fx-background-color: " + toLinearGradient(baseBgStart, baseBgEnd) + ";"
                + "-fx-border-color: " + toCss(baseBorder) + ";");

        Timeline fadeIn = new Timeline(
                new KeyFrame(HOVER_DURATION, new KeyValue(progress, 1, Interpolator.EASE_BOTH)));
        Timeline fadeOut = new Timeline(
                new KeyFrame(HOVER_DURATION, new KeyValue(progress, 0, Interpolator.EASE_BOTH)));

        button.hoverProperty().addListener((obs, wasHover, isHover) -> {
            if (isHover) {
                fadeOut.stop();
                fadeIn.playFromStart();
            } else {
                fadeIn.stop();
                fadeOut.playFromStart();
            }
        });
    }

    private String toLinearGradient(Color start, Color end) {
        if (start.equals(end)) return toCss(start);
        return "linear-gradient(" + toCss(start) + ", " + toCss(end) + ")";
    }

    private String toCss(Color color) {
        return String.format(
                "rgba(%d,%d,%d,%.3f)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                color.getOpacity()
        );
    }
}

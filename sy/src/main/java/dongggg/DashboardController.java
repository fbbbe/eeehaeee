package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import dongggg.MascotProvider;

public class DashboardController {

    @FXML
    private Button noteManageButton;
    @FXML
    private Button newNoteButton;
    @FXML
    private Button quizButton;
    @FXML
    private Label levelValueLabel;
    @FXML
    private Label levelHelperLabel;
    @FXML
    private ProgressBar levelProgressBar;
    @FXML
    private Label conceptNoteCountLabel;
    @FXML
    private Label examCountLabel;
    @FXML
    private Label accuracyLabel;
    @FXML
    private ImageView mascotImageView;

    @FXML
    private void initialize() {
        if (noteManageButton != null) {
            HoverEffects.installPurpleHover(noteManageButton);
        }
        if (newNoteButton != null) {
            HoverEffects.installYellowHover(newNoteButton);
        }
        if (newNoteButton != null) {
            HoverEffects.installYellowHover(newNoteButton);
        }
        if (quizButton != null) {
            HoverEffects.installPinkHover(quizButton);
        }
        updateLevelCard();
        updateConceptNoteCount();
        updateExamCount();
        updateAccuracy();
        updateMascotImage(DonggriRepository.getLevelInfo().getCurrentLevel());

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

    /** 🔥 동그리 옷장 이동 */
    @FXML
    private void openWardrobe() {
        App.showWardrobeView();
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

        updateMascotImage(info.getCurrentLevel());
    }

    private void updateMascotImage(int level) {
        if (mascotImageView == null)
            return;
        mascotImageView.setImage(MascotProvider.loadForLevel(level));
    }

    private void updateExamCount() {
        int examCount = DonggriRepository.getExamCount();
        if (examCountLabel != null) {
            examCountLabel.setText(String.valueOf(examCount));
        }
    }

    private void updateAccuracy() {
        int accuracy = DonggriRepository.getAccuracyPercent();
        if (accuracyLabel != null) {
            accuracyLabel.setText(accuracy + "%");
        }
    }

}

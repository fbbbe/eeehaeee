package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import java.util.Random;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class WardrobeController {

    @FXML
    private Label currentLevelLabel;
    @FXML
    private ImageView currentSkinImage;
    @FXML
    private Label currentSkinNameLabel;
    @FXML
    private Label currentSkinDescLabel;
    @FXML
    private HBox skinListBox;
    @FXML
    private Button confirmButton;
    @FXML
    private Button back;
    @FXML
    private Pane effectLayer;

    private final Random random = new Random();

    private int currentLevel = 1;
    private static final List<String> SKIN_NAMES = List.of(
            "동그리",
            "외계인 동그리",
            "토마토 동그리",
            "일상복 동그리",
            "곰돌이 동그리",
            "신사 동그리",
            "공대생 동그리",
            "감기 동그리",
            "해파리 동그리",
            "학생 동그리",
            "잠자는 동그리",
            "유치원생 동그리",
            "농구 동그리",
            "도둑 동그리",
            "짱구 동그리",
            "하츄핑 동그리",
            "메타몽 동그리",
            "경찰 동그리",
            "코난 동그리",
            "음식 동그리",
            "졸업 동그리");

    @FXML
    private void initialize() {
        if (back != null) {
            HoverEffects.installPurpleHover(back);
        }
        DonggriLevelInfo info = DonggriRepository.getLevelInfo();
        currentLevel = info.getCurrentLevel();
        if (currentLevelLabel != null) {
            currentLevelLabel.setText("현재 레벨: " + currentLevel);
        }
        refill();
    }

    private void refill() {
        List<MascotSkin> skins = MascotSkinRepository.getSkinsWithState(currentLevel);
        MascotSkin active = MascotSkinRepository.resolveSkinForLevel(currentLevel);
        int activeIndex = findIndexByThreshold(skins, active.getLevelThreshold());
        String activeName = skinNameForIndex(activeIndex);

        if (currentSkinImage != null) {
            currentSkinImage.setImage(loadImage(active.getImagePath()));
        }
        if (currentSkinNameLabel != null) {
            currentSkinNameLabel.setText(activeName);
        }
        if (currentSkinDescLabel != null) {
            currentSkinDescLabel.setText("동그리 스타일");
        }

        if (skinListBox != null) {
            skinListBox.getChildren().clear();
            for (int i = 0; i < skins.size(); i++) {
                MascotSkin skin = skins.get(i);
                skinListBox.getChildren()
                        .add(buildSkinCard(skin, skin.getLevelThreshold() == active.getLevelThreshold(), i));
            }
        }
    }

    private void playHearts() {
        // 한 번 누를 때 여러 개 뿜고 싶으면 개수만 바꾸면 됨
        for (int i = 0; i < 4; i++) {
            spawnHeart();
        }
    }

    private void spawnHeart() {
        if (effectLayer == null || effectLayer.getScene() == null) {
            return;
        }

        // 💖 하트 노드 생성
        Label heart = new Label("❤");
        heart.getStyleClass().add("floating-heart");

        double layerWidth = effectLayer.getWidth() > 0
                ? effectLayer.getWidth()
                : effectLayer.getScene().getWidth();
        double layerHeight = effectLayer.getHeight() > 0
                ? effectLayer.getHeight()
                : effectLayer.getScene().getHeight();

        // 시작 위치: 화면 아래쪽 (아래에서 랜덤 X 위치로 올라오게)
        double startX = 40 + random.nextDouble() * (layerWidth - 80);
        double startY = layerHeight + 40;

        heart.setLayoutX(startX);
        heart.setLayoutY(startY);
        effectLayer.getChildren().add(heart);

        // 위로 날아가는 목표 Y
        double targetY = -80 - random.nextDouble() * 80;
        Duration duration = Duration.seconds(2 + random.nextDouble());

        TranslateTransition move = new TranslateTransition(duration, heart);
        move.setFromY(0);
        move.setToY(targetY - startY);

        FadeTransition fade = new FadeTransition(duration, heart);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        ScaleTransition scale = new ScaleTransition(duration, heart);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.2);
        scale.setToY(1.2);

        ParallelTransition combo = new ParallelTransition(move, fade, scale);
        combo.setOnFinished(e -> effectLayer.getChildren().remove(heart));
        combo.play();
    }

    private StackPane buildSkinCard(MascotSkin skin, boolean selected, int index) {
        boolean unlocked = skin.isUnlocked();

        StackPane card = new StackPane();
        card.getStyleClass().add("wardrobe-skin-card");
        if (selected && unlocked)
            card.getStyleClass().add("selected");
        if (!unlocked)
            card.getStyleClass().add("locked");
        if (unlocked)
            card.getStyleClass().add("unlocked");

        VBox box = new VBox(6);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView img = new ImageView(loadImage(skin.getImagePath()));
        img.setFitWidth(80);
        img.setFitHeight(80);
        img.setPreserveRatio(true);
        img.getStyleClass().add("wardrobe-skin-thumb");
        if (!unlocked) {
            img.setOpacity(0.08); // 잠금 상태에서는 거의 보이지 않게
        }

        Label name = new Label();
        name.getStyleClass().add("wardrobe-skin-name");
        if (unlocked) {
            name.setText(skinNameForIndex(index));
        } else {
            // 잠긴 카드에는 아래 오버레이에만 레벨 표기 → 텍스트 숨김
            name.setVisible(false);
            name.setManaged(false);
        }

        box.getChildren().addAll(img, name);
        card.getChildren().add(box);

        if (!unlocked) {
            VBox lockOverlay = new VBox(4);
            lockOverlay.setAlignment(javafx.geometry.Pos.CENTER);
            lockOverlay.getStyleClass().add("wardrobe-lock-overlay");
            // SVG 기반 잠금 아이콘
            SVGPath lockShape = new SVGPath();
            lockShape.setContent(
                    "M3 11 " + // 시작점: 자물쇠 바디 왼쪽 위
                            "h18 " + // 가로로 18px (바디 상단)
                            "a2 2 0 0 1 2 2 " + // 오른쪽 위 모서리 라운드
                            "v7 " + // 아래로 7px
                            "a2 2 0 0 1 -2 2 " + // 오른쪽 아래 모서리 라운드
                            "h-18 " + // 왼쪽으로 18px
                            "a2 2 0 0 1 -2 -2 " + // 왼쪽 아래 모서리 라운드
                            "v-7 " + // 위로 7px
                            "a2 2 0 0 1 2 -2 " + // 왼쪽 위 모서리 라운드
                            "z " + // 바디 닫기
                            // 자물쇠 고리
                            "M7 11 " +
                            "V7 " +
                            "a5 5 0 0 1 10 0 " +
                            "v4");
            lockShape.setFill(Color.TRANSPARENT);
            lockShape.setStroke(Color.web("#D9B5FF"));
            lockShape.setStrokeWidth(2.0);

            StackPane lockIcon = new StackPane(lockShape);
            lockIcon.getStyleClass().add("wardrobe-lock-icon");

            Label lockText = new Label("Lv." + skin.getLevelThreshold());
            lockText.getStyleClass().add("wardrobe-lock-text");

            lockOverlay.getChildren().addAll(lockIcon, lockText);
            card.getChildren().add(lockOverlay);
        } else {
            card.setOnMouseClicked(e -> {
                MascotSkinRepository.selectSkin(skin.getLevelThreshold(), currentLevel);
                refill();
            });
        }

        return card;
    }

    private Image loadImage(String path) {
        try {
            Image img = new Image(path, true);
            if (img.isError()) {
                return new Image("dongggg/images/dong1.png");
            }
            return img;
        } catch (Exception e) {
            return new Image("dongggg/images/dong1.png");
        }
    }

    @FXML
    private void onBack() {
        App.showDashboardView();
    }

    @FXML
    private void onConfirm() {
        playHearts();
    }

    private String skinNameForIndex(int index) {
        if (index >= 0 && index < SKIN_NAMES.size()) {
            return SKIN_NAMES.get(index);
        }
        return "동그리";
    }

    private int findIndexByThreshold(List<MascotSkin> skins, int threshold) {
        for (int i = 0; i < skins.size(); i++) {
            if (skins.get(i).getLevelThreshold() == threshold) {
                return i;
            }
        }
        return 0;
    }
}

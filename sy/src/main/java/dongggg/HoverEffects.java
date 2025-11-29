package dongggg;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * 공통 hover 이펙트를 모아 둔 유틸 클래스.
 * - 보라색 계열 hover: 노트 카드 등
 * - 노란색 계열 hover: 폴더 카드 등
 *
 * 원하는 컨트롤러 어디에서든 HoverEffects.installPurpleHover(region),
 * HoverEffects.installYellowHover(region) 으로 재사용 가능.
 */
public class HoverEffects {

    // 모든 hover 이펙트에 공통으로 쓸 애니메이션 시간
    private static final Duration HOVER_DURATION = Duration.millis(240);

    // 그림자 색상(보라 계열)만 고정으로 사용
    private static final Color SHADOW_BASE_COLOR = Color.rgb(168, 85, 221);

    /** 💜 노트 카드 등에 쓰는 보라 계열 hover 효과 */
    public static void installPurpleHover(Region region) {
        installHoverAnimation(
                region,
                Color.web("#ffffff", 0.98), // 기본 배경
                Color.web("#f5f0fb"), // hover 배경
                Color.web("#e8dff5"), // 기본 테두리
                Color.web("#a855dd"), // hover 테두리
                6, 18, // 그림자 radius from 6 -> 18
                0.05, 0.22 // 그림자 투명도 from 0.05 -> 0.22
        );
    }

    /** 💛 폴더 카드 등에 쓰는 노란 계열 hover 효과 */
    public static void installYellowHover(Region region) {
        installHoverAnimation(
                region,
                Color.web("#ffffff"), // 기본 배경
                Color.web("#fff9e6"), // hover 배경 (옅은 노랑)
                Color.web("#e8dff5"), // 기본 테두리 (연보라)
                Color.web("#ffb547"), // hover 테두리 (노랑)
                6, 18,
                0.05, 0.20);
    }

    public static void installPinkHover(Region region) {
        installHoverAnimation(
                region,
                Color.web("#ffffff"), // 기본 배경
                Color.web("rgba(253, 242, 254, 1)"), // hover 배경 (옅은 노랑)
                Color.web("#e8dff5"), // 기본 테두리 (연보라)
                Color.web("rgba(207, 137, 213, 1);"), // hover 테두리 (노랑)
                6, 18,
                0.05, 0.20);
    }

    /**
     * 실제 애니메이션 구현부 (공통).
     * base/hover 색과 그림자 값을 받아서, hover 상태에 따라 서서히 보간해 준다.
     */
    private static void installHoverAnimation(Region region,
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
        shadow.setColor(SHADOW_BASE_COLOR.deriveColor(0, 1, 1, baseShadowOpacity));
        region.setEffect(shadow);

        // 초기 스타일 세팅
        region.setStyle(baseStyle
                + "-fx-background-color: " + toCss(baseBackground) + ";"
                + "-fx-border-color: " + toCss(baseBorder) + ";");

        // progress 값이 0 → 1로 갈 때마다 색/그림자 업데이트
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
            shadow.setColor(SHADOW_BASE_COLOR.deriveColor(0, 1, 1, opacity));
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

    /** JavaFX Color → CSS rgba(...) 문자열로 변환 */
    private static String toCss(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        double opacity = Math.round(color.getOpacity() * 1000) / 1000.0;
        return String.format("rgba(%d,%d,%d,%.3f)", r, g, b, opacity);
    }
}
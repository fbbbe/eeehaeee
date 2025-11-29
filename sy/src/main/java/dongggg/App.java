package dongggg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {

    private static Scene scene;
    private static Stage stage;

    private static final String MAIN_STYLESHEET =
            App.class.getResource("styles.css").toExternalForm();
    private static final Color ROOT_BACKGROUND = Color.web("#fdf4ff");

    @Override
    public void start(Stage stage) throws IOException {
        Database.init();

        App.stage = stage;

        FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        Parent root = loader.load();

        scene = new Scene(root, 1200, 720);
        reloadStylesheet();

        stage.setTitle("동그리 노트");
        stage.setScene(scene);
        stage.show();
    }

    public static Scene getScene() {
        return scene;
    }

    private static void replaceSceneRoot(Parent root) {
        swapRootKeepingState(root);
    }

    public static Parent swapRootKeepingState(Parent newRoot) {
        boolean wasFullScreen = stage != null && stage.isFullScreen();
        boolean wasMaximized = stage != null && stage.isMaximized();
        double prevWidth = stage != null ? stage.getWidth() : 1200;
        double prevHeight = stage != null ? stage.getHeight() : 720;

        Parent previousRoot = scene != null ? scene.getRoot() : null;

        if (scene == null) {
            scene = new Scene(newRoot, 1200, 720);
            reloadStylesheet();
            if (stage != null) {
                stage.setScene(scene);
            }
        } else {
            scene.setRoot(newRoot);
            reloadStylesheet();
        }

        if (stage != null) {
            if (wasFullScreen) {
                stage.setFullScreen(true);
            } else {
                stage.setMaximized(wasMaximized);
                if (!wasMaximized && prevWidth > 0 && prevHeight > 0) {
                    stage.setWidth(prevWidth);
                    stage.setHeight(prevHeight);
                }
            }
        }

        return previousRoot;
    }

    public static void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showNoteEditor(Note note) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("note-detail-view.fxml"));
            Parent root = loader.load();

            NoteDetailController controller = loader.getController();
            controller.setNote(note);

            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showConceptNoteEditor(Note note) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("concept-note-view.fxml"));
            Parent root = loader.load();

            ConceptNoteController controller = loader.getController();
            controller.setNote(note);

            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showNoteTypeSelect() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("note-type-select-view.fxml"));
            Parent root = loader.load();
            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 🔥 대시보드 전환 */
    public static void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            replaceSceneRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 🔥 시험 시작 화면 전환 */
    public static void showQuizStartView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("quiz-start-view.fxml"));
            Parent root = loader.load();
            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showWardrobeView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("wardrobe-view.fxml"));
            Parent root = loader.load();
            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showFolderCreateView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("folder-create-view.fxml"));
            Parent root = loader.load();
            replaceSceneRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void reloadStylesheet() {
        if (scene != null) {
            scene.getStylesheets().clear();
            scene.getStylesheets().add(MAIN_STYLESHEET);
            scene.setFill(ROOT_BACKGROUND);
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}

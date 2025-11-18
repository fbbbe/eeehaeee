package dongggg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Database.init();
        NoteRepository.ensureSampleData();

        FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        Parent root = loader.load();

        scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(
                App.class.getResource("styles.css").toExternalForm()
        );

        stage.setTitle("동그리 노트");
        stage.setScene(scene);
        stage.show();
    }

    public static Scene getScene() {
        return scene;
    }

    public static void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            scene.setRoot(root);
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

            scene.setRoot(root);
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

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showNoteTypeSelect() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("note-type-select-view.fxml"));
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔥🔥 여기! 대시보드 전환 기능
    public static void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            scene.setRoot(root);

            // ⭐ CSS 강제 재적용
            scene.getStylesheets().clear();
            scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 🔥 시험 시작 화면 전환 기능
    public static void showQuizStartView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("quiz-start-view.fxml"));
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}

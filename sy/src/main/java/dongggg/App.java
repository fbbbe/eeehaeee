package dongggg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 앱 전체에서 단 하나의 Stage/Scene만 쓰고,
 * FXML root만 갈아끼우면서 화면을 전환하는 역할을 하는 클래스.
 */

// ❤️ 이 클래스가 메인 클래스로 javafx에 있는 Application라는 추상 클래스를 상속받는다. (Application의 구조는
// 노션 참고)
public class App extends Application {

    // 메인 윈도우에서 공유할 단 하나의 Scene
    private static Scene scene;

    @Override // ❤️ 여기서 start()메소드는 Application에서 작성된 메소드로 오버라이딩 한다(재정의)
    public void start(Stage stage) throws IOException {
        // DB 초기화 및 샘플 데이터 준비
        Database.init();
        NoteRepository.ensureSampleData();

        // 처음에는 메인 화면(main-view.fxml)을 로드
        FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
        Parent root = loader.load();

        // scene, stage와 관련된 건 노션 참고
        scene = new Scene(root, 1200, 720);
        scene.getStylesheets().add(
                App.class.getResource("styles.css").toExternalForm());

        stage.setTitle("동그리 노트");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 현재 사용 중인 Scene을 리턴.
     * (Alert 띄울 때 owner 설정 등에 사용할 수 있음)
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * 메인 화면으로 이동.
     * main-view.fxml 을 다시 로드하기 때문에
     * MainController.initialize()가 다시 실행되고
     * 최근 노트 목록도 자동으로 리프레시됨.
     */
    public static void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("main-view.fxml"));
            Parent root = loader.load();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 일반 노트 편집 화면으로 이동.
     *
     * @param note 편집할 노트 객체 (null이면 "새 노트" 모드로 들어감)
     */
    public static void showNoteEditor(Note note) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("note-detail-view.fxml"));
            Parent root = loader.load();

            NoteDetailController controller = loader.getController();
            controller.setNote(note); // null이면 새 노트 모드

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 개념 노트 편집 화면으로 이동.
     *
     * @param note 편집할 노트 객체 (null이면 "새 개념 노트" 모드)
     */
    public static void showConceptNoteEditor(Note note) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("concept-note-view.fxml"));
            Parent root = loader.load();

            ConceptNoteController controller = loader.getController();
            controller.setNote(note); // null이면 새 개념 노트 모드

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 노트 유형 선택 화면으로 이동.
     * (일반 노트 / 개념 노트 중 하나 선택하는 페이지)
     */
    public static void showNoteTypeSelect() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("note-type-select-view.fxml"));
            Parent root = loader.load();

            // 컨트롤러 쪽에서 따로 초기화할 게 있으면 여기서 가져와서 설정 가능
            // NoteTypeSelectController controller = loader.getController();

            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
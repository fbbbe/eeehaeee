package dongggg;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// ❤️ 이 클래스가 메인 클래스로 javafx에 있는 Application라는 추상 클래스를 상속받는다. (Application의 구조는 노션 참고)
public class App extends Application {

    @Override // ❤️ 여기서 start()메소드는 Application에서 작성된 메소드로 오버라이딩 한다(재정의)
    public void start(Stage stage) throws Exception {
        // 1) DB 초기화
        Database.init();

        // 2) FXML 로딩
        // resources/dongggg/main-view.fxml 를 읽어온다
        FXMLLoader loader = new FXMLLoader(
                App.class.getResource("main-view.fxml") // 또는 "/dongggg/main-view.fxml" 도 가능
        );

        // scene, stage와 관련된 건 노션 참고
        Scene scene = new Scene(loader.load(), 1200, 700);
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());

        stage.setTitle("동그리 노트");
        stage.setScene(scene);
        stage.show();
        // 관련 메소드들은 걍 외우는 수밖에 없음ㅜ
    }

    public static void main(String[] args) {
        launch(args); // launch() -> 메인 클래스 객체, 메인 윈도우 생성, start() 호출
    }
}

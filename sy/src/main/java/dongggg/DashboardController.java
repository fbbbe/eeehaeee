package dongggg;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class DashboardController {

    @FXML
    private void goNoteManager(ActionEvent e) {
        App.showMainView();  // 예: 노트 관리 = 기존 메인 화면
    }

    @FXML
    private void goConceptNote(ActionEvent e) {
        App.showConceptNoteEditor(null);
    }

    @FXML
    private void goQuiz(ActionEvent e) {
        App.showNoteTypeSelect();  // 시험 시작 페이지 연결되면 수정
    }
}

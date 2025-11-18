package dongggg;

import javafx.fxml.FXML;

public class DashboardController {

    @FXML
    private void goNoteManager() {
        App.showMainView();
    }

    @FXML
    private void goConceptNote() {
        App.showNoteTypeSelect();
    }

    // 🔥 시험 보기 버튼 → 시험 시작 화면으로 이동
    @FXML
    private void goQuiz() {
        App.showQuizStartView();
    }
}

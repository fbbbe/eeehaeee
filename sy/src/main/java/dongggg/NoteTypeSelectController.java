
package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * "+ 버튼"을 눌렀을 때 뜨는
 * "일반 노트 / 개념 노트 선택 화면"을 담당하는 컨트롤러.
 *
 * 이 화면도 새 창을 띄우지 않고
 * App.showNoteTypeSelect() 로 현재 Scene의 root만 교체하는 방식이다.
 */
public class NoteTypeSelectController {

    @FXML
    private Button normalButton;

    @FXML
    private Button conceptButton;

    @FXML
    public void initialize() {
        // 필요하면 여기서 버튼 상태 초기화 가능
    }

    @FXML
    private void onNormalNote() {
        // 새 "일반 노트" 편집 화면으로 전환
        App.showNoteEditor(null);
    }

    @FXML
    private void onConceptNote() {
        // 새 "개념 노트" 편집 화면으로 전환
        App.showConceptNoteEditor(null);
    }

    @FXML
    private void onBack() {
        // 메인 화면으로 되돌아가기
        App.showMainView();
    }
}
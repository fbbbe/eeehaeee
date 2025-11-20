package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

/**
 * 개념 노트 화면 컨트롤러.
 *
 * - 왼쪽 세로 컬럼: 개념 입력 TextArea 들 (conceptContainer)
 * - 오른쪽 세로 컬럼: 설명 입력 TextArea 들 (explanationContainer)
 * - 하단 + 버튼: 양쪽에 새 입력 행을 하나씩 추가
 * - 가운데 세로선은 SplitPane divider (FXML에서 orientation=HORIZONTAL)
 */
public class ConceptNoteController {

    @FXML
    private TextField titleField;

    @FXML
    private VBox conceptContainer; // 왼쪽 개념 입력 칼럼

    @FXML
    private VBox explanationContainer; // 오른쪽 설명 입력 칼럼

    @FXML
    private Label statusLabel;

    private Note note; // 필요하면 기존 노트 편집용
    private boolean initialized = false;

    // 현재 화면에 존재하는 개념/설명 입력 행들을 관리
    private final List<ConceptRow> rows = new ArrayList<>();

    public void setNote(Note note) {
        this.note = note;
        if (initialized) {
            loadExistingNote();
        }
    }

    @FXML
    public void initialize() {
        // 화면 열릴 때 최소 1개의 입력 행을 만들어 둔다.
        addEmptyRow();
        initialized = true;
        if (note != null) {
            loadExistingNote();
        }
    }

    /** 하단 + 버튼 클릭 시: 새 개념/설명 행 추가 */
    @FXML
    private void onAddPair() {
        addEmptyRow();
    }

    /** 저장 버튼 클릭 시 */
    @FXML
    private void onSaveAll() {
        String title = titleField.getText() != null ? titleField.getText().trim() : "";

        if (title.isEmpty()) {
            showAlert("제목을 입력해주세요.");
            return;
        }

        // 입력된 개념/설명 쌍 모으기
        List<ConceptPairData> dataList = new ArrayList<>();
        for (ConceptRow row : rows) {
            String term = row.termArea.getText() != null ? row.termArea.getText().trim() : "";
            String explanation = row.explanationArea.getText() != null ? row.explanationArea.getText().trim() : "";

            if (term.isEmpty() && explanation.isEmpty()) {
                continue;
            }
            dataList.add(new ConceptPairData(term, explanation));
        }

        if (dataList.isEmpty()) {
            showAlert("최소 한 개 이상의 개념/설명을 입력해주세요.");
            return;
        }

        // 첫 번째 설명을 노트 콘텐츠 요약으로 사용
        String contentSummary = dataList.get(0).explanation;

        if (note == null) {
            // 새 노트 생성
            note = new Note(title, contentSummary, "CONCEPT");
            NoteRepository.insert(note);
        } else {
            // 기존 노트 수정
            note.setTitle(title);
            note.setContent(contentSummary);
            NoteRepository.update(note);
        }

        // 개념-설명 페어 저장 (정렬 순서 유지)
        List<ConceptPair> pairs = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            ConceptPairData d = dataList.get(i);
            pairs.add(new ConceptPair(0, note.getId(), d.term, d.explanation, i));
        }
        ConceptPairRepository.replaceAllForNote(note.getId(), pairs);

        statusLabel.setText("개념 노트가 저장되었습니다.");
        App.showMainView();
    }

    /** 닫기 버튼 → 메인 화면으로 */
    @FXML
    private void onCancel() {
        App.showMainView();
    }

    /**
     * 실제로 양쪽 컬럼에 새 입력 행을 추가하는 내부 메서드.
     */
    private void addEmptyRow() {
        addRowWithValue("", "");
    }

    private void addRowWithValue(String term, String explanation) {
        // 왼쪽: 개념 입력 TextArea
        TextArea termArea = new TextArea();
        termArea.setPromptText("Ex. 데이터의 정의");
        termArea.setWrapText(true);
        termArea.getStyleClass().addAll("concept-input", "note-pill-input");

        // 오른쪽: 설명 입력 TextArea
        TextArea explanationArea = new TextArea();
        explanationArea.setPromptText("설명을 입력하세요");
        explanationArea.setWrapText(true);
        explanationArea.getStyleClass().addAll("explanation-input", "note-pill-input");

        // 처음에는 한 줄만 보이도록
        termArea.setPrefRowCount(1);
        explanationArea.setPrefRowCount(1);

        termArea.setText(term);
        explanationArea.setText(explanation);

        // 행 객체 하나 만들어서 리스트에 넣어두기
        ConceptRow row = new ConceptRow(termArea, explanationArea);
        rows.add(row);

        // 텍스트가 바뀔 때마다 이 행의 높이를 맞춘다.
        termArea.textProperty().addListener((obs, oldText, newText) -> syncRowHeight(row));
        explanationArea.textProperty().addListener((obs, oldText, newText) -> syncRowHeight(row));

        // 컨테이너에 추가
        conceptContainer.getChildren().add(termArea);
        explanationContainer.getChildren().add(explanationArea);
    }

    private void loadExistingNote() {
        if (note == null || !initialized) return;

        titleField.setText(note.getTitle() != null ? note.getTitle() : "");

        rows.clear();
        conceptContainer.getChildren().clear();
        explanationContainer.getChildren().clear();

        List<ConceptPair> pairs = ConceptPairRepository.findByNoteId(note.getId());
        if (pairs.isEmpty()) {
            addEmptyRow();
            return;
        }

        pairs.stream()
                .sorted(Comparator.comparingInt(ConceptPair::getSortOrder))
                .forEach(p -> addRowWithValue(
                        p.getTerm() != null ? p.getTerm() : "",
                        p.getExplanation() != null ? p.getExplanation() : ""));
    }

    // 한 행의 개념/설명 TextArea 높이를 "같은 줄 수"로 맞춰주는 함수
    private void syncRowHeight(ConceptRow row) {
        TextArea termArea = row.termArea;
        TextArea explanationArea = row.explanationArea;

        // 각 TextArea의 실제 줄 수(문단 수)를 구함
        int termLines = Math.max(1, termArea.getParagraphs().size());
        int expLines = Math.max(1, explanationArea.getParagraphs().size());

        // 둘 중 더 큰 줄 수만큼 높이를 맞춘다
        int rowsCount = Math.max(termLines, expLines);

        termArea.setPrefRowCount(rowsCount);
        explanationArea.setPrefRowCount(rowsCount);
    }

    // TextArea 안에 줄 수가 늘어나면 그에 맞춰서 줄 수(prefRowCount)를 늘려주는 함수
    private void autoGrowRows(TextArea area, String text) {
        if (text == null || text.isEmpty()) {
            area.setPrefRowCount(1);
            return;
        }

        // 엔터 기준으로 줄 수 계산 (wrap까지 완벽히 반영하는 건 아니지만, 사용성에는 충분함)
        int lines = text.split("\n", -1).length;

        // 너무 미친 듯이 커지지 않게 적당히 상한만 걸어둠 (원하면 없애도 됨)
        int clamped = Math.max(1, Math.min(lines, 10));

        area.setPrefRowCount(clamped);
    }

    /** 간단한 Alert 헬퍼 (지금은 상태 라벨 + 콘솔 출력) */
    private void showAlert(String message) {
        statusLabel.setText(message);
        System.out.println("[ConceptNote][ALERT] " + message);
    }

    /** 한 행을 구성하는 개념/설명 TextArea 쌍 */
    private static class ConceptRow {
        final TextArea termArea;
        final TextArea explanationArea;

        ConceptRow(TextArea termArea, TextArea explanationArea) {
            this.termArea = termArea;
            this.explanationArea = explanationArea;
        }
    }

    /** 저장 시 사용하기 위한 단순 데이터 구조체 */
    private static class ConceptPairData {
        final String term;
        final String explanation;

        ConceptPairData(String term, String explanation) {
            this.term = term;
            this.explanation = explanation;
        }
    }
}

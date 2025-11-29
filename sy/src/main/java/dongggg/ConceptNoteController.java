package dongggg;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.geometry.Side;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Comparator;
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
    private BorderPane root;

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

    @FXML
    private Button cancelb;

    @FXML
    private Button sortButton;

    @FXML
    private Button deleteButton;

    // 현재 화면에 존재하는 개념/설명 입력 행들을 관리
    private final List<ConceptRow> rows = new ArrayList<>();
    private SortMode sortMode = SortMode.DEFAULT;
    private ContextMenu sortMenu;

    public void setNote(Note note) {
        this.note = note;
        if (initialized) {
            loadExistingNote();
            deferBlurInputs();
        }
    }

    @FXML
    public void initialize() {
        if (cancelb != null) {
            HoverEffects.installPurpleHover(cancelb);
        }
        if (deleteButton != null) {
            deleteButton.setDisable(true);
        }
        // 화면 열릴 때 최소 1개의 입력 행을 만들어 둔다.
        addEmptyRow();
        renderRows();
        initialized = true;
        if (note != null) {
            loadExistingNote();
        }
        updateSortMenuLabel();
        deferBlurInputs();
    }

    /** 하단 + 버튼 클릭 시: 새 개념/설명 행 추가 */
    @FXML
    private void onAddPair() {
        addEmptyRow();
        renderRows();
    }

    @FXML
    private void onSortDefault() {
        setSortMode(SortMode.DEFAULT);
    }

    @FXML
    private void onSortWrongRate() {
        setSortMode(SortMode.WRONG_RATE);
    }

    @FXML
    private void onOpenSortMenu() {
        if (sortButton == null) {
            return;
        }

        if (sortMenu != null) {
            sortMenu.hide();
        }

        sortMenu = buildSortMenu();
        sortMenu.getStyleClass().add("note-context-menu");
        sortMenu.setOnHidden(e -> sortMenu = null);
        sortMenu.show(sortButton, Side.TOP, 0, 0);
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
        for (ConceptRow row : rows.stream()
                .sorted(Comparator.comparingInt(r -> r.sortOrder))
                .toList()) {
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

    @FXML
    private void onDelete() {
        if (note == null) {
            App.showMainView();
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("삭제 확인");
        alert.setHeaderText("노트를 삭제할까요?");
        alert.setContentText("이 작업은 되돌릴 수 없습니다.");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                NoteRepository.delete(note.getId());
                App.showMainView();
            }
        });
    }

    /**
     * 실제로 양쪽 컬럼에 새 입력 행을 추가하는 내부 메서드.
     */
    private void addEmptyRow() {
        addRowWithValue("", "", nextSortOrder(), 0.0, null);
    }

    private void addRowWithValue(String term, String explanation, int sortOrder, double wrongRate, Integer pairId) {
        addRowWithValue(term, explanation, sortOrder, wrongRate, pairId, 0);
    }

    private void addRowWithValue(String term, String explanation, int sortOrder, double wrongRate, Integer pairId, int totalAttempts) {
        // 왼쪽: 개념 입력 TextArea
        TextArea termArea = new TextArea();
        termArea.setPromptText("Ex. 데이터의 정의");
        termArea.setWrapText(true);
        termArea.getStyleClass().addAll("concept-input", "note-pill-input");
        termArea.setMinWidth(0);

        // 오른쪽: 설명 입력 TextArea
        TextArea explanationArea = new TextArea();
        explanationArea.setPromptText("설명을 입력하세요");
        explanationArea.setWrapText(true);
        explanationArea.getStyleClass().addAll("explanation-input", "note-pill-input");
        explanationArea.setMinWidth(0);

        // 기본 행 높이
        termArea.setPrefRowCount(2);
        explanationArea.setPrefRowCount(2);

        termArea.setText(term);
        explanationArea.setText(explanation);

        // 행 객체 하나 만들어서 리스트에 넣어두기
        ConceptRow row = new ConceptRow(termArea, explanationArea, sortOrder, wrongRate, pairId, totalAttempts);
        rows.add(row);

        // 텍스트/폭 변화에 맞춰 자동으로 높이를 키워 스크롤 없이 보여준다.
        installAutoResize(row);
    }

    private int nextSortOrder() {
        return rows.stream().mapToInt(r -> r.sortOrder).max().orElse(-1) + 1;
    }

    private void renderRows() {
        if (conceptContainer == null || explanationContainer == null) {
            return;
        }

        List<ConceptRow> ordered = new ArrayList<>(rows);
        if (sortMode == SortMode.WRONG_RATE) {
            Comparator<ConceptRow> byWrongRate = Comparator
                    .comparingDouble((ConceptRow r) -> r.wrongRate)
                    .reversed()
                    .thenComparing(Comparator.comparingInt((ConceptRow r) -> r.totalAttempts).reversed())
                    .thenComparingInt(r -> r.sortOrder)
                    .thenComparingInt(r -> r.pairId != null ? r.pairId : Integer.MAX_VALUE);
            ordered.sort(byWrongRate);
        } else {
            ordered.sort(Comparator.comparingInt(r -> r.sortOrder));
        }

        conceptContainer.getChildren().clear();
        explanationContainer.getChildren().clear();

        for (ConceptRow row : ordered) {
            conceptContainer.getChildren().add(row.termArea);
            explanationContainer.getChildren().add(row.explanationArea);
        }
    }

    private void setSortMode(SortMode mode) {
        sortMode = mode;
        if (sortMenu != null) {
            sortMenu.hide();
        }
        updateSortMenuLabel();
        renderRows();
    }

    private void updateSortMenuLabel() {
        if (sortButton == null) {
            return;
        }
        String label = sortMode == SortMode.WRONG_RATE ? "정렬: 오답률순" : "정렬: 기본순";
        sortButton.setText(label);
    }

    private void loadExistingNote() {
        if (note == null || !initialized)
            return;

        titleField.setText(note.getTitle() != null ? note.getTitle() : "");

        rows.clear();
        conceptContainer.getChildren().clear();
        explanationContainer.getChildren().clear();

        sortMode = SortMode.DEFAULT;

        List<ConceptPair> pairs = ConceptPairRepository.findByNoteId(note.getId());
        if (pairs.isEmpty()) {
            addEmptyRow();
            renderRows();
            updateSortMenuLabel();
            if (deleteButton != null) {
                deleteButton.setDisable(false);
            }
            return;
        }

        pairs.stream()
                .sorted(Comparator.comparingInt(ConceptPair::getSortOrder))
                .forEach(p -> addRowWithValue(
                        p.getTerm() != null ? p.getTerm() : "",
                        p.getExplanation() != null ? p.getExplanation() : "",
                        p.getSortOrder(),
                        p.getWrongRate(),
                        p.getId(),
                        p.getTotalAttempts()));
        renderRows();
        updateSortMenuLabel();
        if (deleteButton != null) {
            deleteButton.setDisable(false);
        }
    }

    private void installAutoResize(ConceptRow row) {
        Runnable resize = () -> resizeRowToContent(row);
        row.termArea.textProperty().addListener((obs, o, n) -> resize.run());
        row.explanationArea.textProperty().addListener((obs, o, n) -> resize.run());
        row.termArea.widthProperty().addListener((obs, o, n) -> resize.run());
        row.explanationArea.widthProperty().addListener((obs, o, n) -> resize.run());

        // 초기 렌더링 직후 폭이 계산된 상태에서 한번 더 맞춤
        Platform.runLater(resize);
    }

    private void resizeRowToContent(ConceptRow row) {
        double termHeight = computeTextAreaHeight(row.termArea);
        double explanationHeight = computeTextAreaHeight(row.explanationArea);
        double targetHeight = Math.max(termHeight, explanationHeight);

        applyHeight(row.termArea, targetHeight);
        applyHeight(row.explanationArea, targetHeight);
    }

    private void applyHeight(TextArea area, double height) {
        area.setPrefHeight(height);
        area.setMinHeight(Region.USE_PREF_SIZE);
        area.setMaxHeight(Double.MAX_VALUE);
    }

    private double computeTextAreaHeight(TextArea area) {
        String text = area.getText();
        if (text == null || text.isEmpty()) {
            text = " ";
        }

        double measuredWidth = area.getWidth();
        double minMeasureWidth = 360; // collapse 시에도 높이가 급변하지 않도록 최소 측정 폭 유지
        double wrappingWidth = Math.max(measuredWidth, minMeasureWidth)
                - area.snappedLeftInset()
                - area.snappedRightInset()
                - 24; // 여유 padding

        Text helper = new Text(text);
        helper.setFont(area.getFont());
        if (wrappingWidth > 0) {
            helper.setWrappingWidth(wrappingWidth);
        }

        double textHeight = helper.getLayoutBounds().getHeight();
        double padding = area.snappedTopInset() + area.snappedBottomInset() + 20;
        double minHeight = 64;

        return Math.max(minHeight, textHeight + padding);
    }

    /** 간단한 Alert 헬퍼 (지금은 상태 라벨 + 콘솔 출력) */
    private void showAlert(String message) {
        statusLabel.setText(message);
        System.out.println("[ConceptNote][ALERT] " + message);
    }

    private enum SortMode {
        DEFAULT,
        WRONG_RATE
    }

    private ContextMenu buildSortMenu() {
        ContextMenu menu = new ContextMenu();

        CustomMenuItem defaultItem = buildSortItem("기본순", this::onSortDefault);

        CustomMenuItem wrongRateItem = buildSortItem("오답률순", this::onSortWrongRate);

        menu.getItems().addAll(defaultItem, wrongRateItem);
        return menu;
    }

    private CustomMenuItem buildSortItem(String text, Runnable action) {
        Label label = new Label(text);
        label.getStyleClass().add("concept-sort-label");

        HBox row = new HBox(label);
        row.setSpacing(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getStyleClass().add("concept-sort-item");

        CustomMenuItem item = new CustomMenuItem(row);
        item.setHideOnClick(true);
        item.setOnAction(e -> action.run());
        return item;
    }

    /** 한 행을 구성하는 개념/설명 TextArea 쌍 */
    private static class ConceptRow {
        final TextArea termArea;
        final TextArea explanationArea;
        final int sortOrder;
        final double wrongRate;
        final Integer pairId;
        final int totalAttempts;

        ConceptRow(TextArea termArea, TextArea explanationArea, int sortOrder, double wrongRate, Integer pairId, int totalAttempts) {
            this.termArea = termArea;
            this.explanationArea = explanationArea;
            this.sortOrder = sortOrder;
            this.wrongRate = wrongRate;
            this.pairId = pairId;
            this.totalAttempts = totalAttempts;
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

    private void deferBlurInputs() {
        Platform.runLater(() -> {
            if (root != null) {
                root.setFocusTraversable(true);
                root.requestFocus();
            }
            if (titleField != null) {
                titleField.deselect();
            }
            for (ConceptRow row : rows) {
                if (row.termArea != null) {
                    row.termArea.deselect();
                }
                if (row.explanationArea != null) {
                    row.explanationArea.deselect();
                }
            }
        });
    }
}

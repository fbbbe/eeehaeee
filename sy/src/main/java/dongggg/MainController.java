package dongggg;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainController {

    @FXML
    private TextField searchField;

    @FXML
    private Button newFolderButton;

    @FXML
    private Button newNoteButton;

    @FXML
    private GridPane folderGrid;

    @FXML
    private VBox recentNotesBox;

    @FXML // 노트 앱 켰을 때 아무것도 없으면 자동 생성되게 하는 거임
    public void initialize() {
        System.out.println("MainController initialize");

        // 1) 샘플 노트 보장
        NoteRepository.ensureSampleData();

        // 2) 최근 노트 5개 목록 로드 매소드 내용은 바로 하단에
        loadRecentNotes();
    }

    private void loadRecentNotes() {
        // VBox 초기화
        recentNotesBox.getChildren().clear();

        // DB에서 최근 노트 5개 조회
        List<Note> notes = NoteRepository.findRecent(5);

        for (Note note : notes) {
            HBox card = createNoteCard(note);
            recentNotesBox.getChildren().add(card);
        }
    }

    // 노트 한 개를 화면에 보여줄 카드 UI 생성
    private HBox createNoteCard(Note note) {
        HBox card = new HBox();
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(8);
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 12; " +
                "-fx-padding: 10 12;");

        VBox textBox = new VBox();
        textBox.setSpacing(4);

        Label titleLabel = new Label(note.getTitle());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // 날짜/태그 부분 (일단 간단히 updatedAt만 표시)
        String dateText = note.getUpdatedAt() != null ? note.getUpdatedAt() : "";
        Label dateLabel = new Label(dateText);
        dateLabel.setStyle("-fx-text-fill: #8080a0;");

        textBox.getChildren().addAll(titleLabel, dateLabel);

        card.getChildren().add(textBox);

        // TODO: 나중에 카드 클릭 시 상세 화면으로 이동하는 이벤트 추가
        // card.setOnMouseClicked(event -> openNoteDetail(note));

        return card;
    }

    @FXML
    private void onNewFolder() {
        System.out.println("새 폴더 버튼 클릭 (추후 구현)");
    }

    @FXML
    private void onNewNote() {
        System.out.println("새 노트 버튼 클릭 - 임시로 샘플 노트 하나 더 생성");
        // 임시: 새 노트 하나 DB에 저장하고 리스트 다시 로드
        NoteRepository.insert(new Note("새 노트", "여기에 노트 내용을 작성할 수 있습니다."));
        loadRecentNotes();
    }
}
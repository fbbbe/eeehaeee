// 시험 결과/채점 처리

package dongggg;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class QuizResultController {

    @FXML private TableView<ResultRow> resultTable;
    @FXML private TableColumn<ResultRow, String> conceptCol;
    @FXML private TableColumn<ResultRow, String> correctCol;
    @FXML private TableColumn<ResultRow, String> userCol;

    public void showResult(List<ConceptPair> quizList, List<String> answers) {

        for (int i = 0; i < quizList.size(); i++) {
            ConceptPair pair = quizList.get(i);

            String userAns = answers.get(i);
            String correctAns = pair.getExplanation();  // 🔥 변경됨

            resultTable.getItems().add(new ResultRow(
                    pair.getTerm(),     // 🔥 변경됨
                    correctAns,
                    userAns
            ));
        }

        conceptCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().concept()));
        correctCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().correct()));
        userCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().user()));
    }

    public record ResultRow(String concept, String correct, String user) {}

    @FXML
    public void goHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("main-view.fxml"));
            Stage stage = (Stage) resultTable.getScene().getWindow();
            stage.setScene(new Scene(root));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

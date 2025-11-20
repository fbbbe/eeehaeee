package dongggg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConceptPairRepository {

    public static List<ConceptPair> findByNoteId(int noteId) {
        List<ConceptPair> list = new ArrayList<>();

        String sql = """
                SELECT id,
                       note_id,
                       term,
                       explanation,
                       sort_order,
                       total_attempts,
                       correct_count,
                       wrong_rate
                FROM concept_pairs
                WHERE note_id = ?
                ORDER BY sort_order ASC, id ASC
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, noteId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ConceptPair pair = new ConceptPair(
                            rs.getInt("id"),
                            rs.getInt("note_id"),
                            rs.getString("term"),
                            rs.getString("explanation"),
                            rs.getInt("sort_order"),
                            rs.getInt("total_attempts"),
                            rs.getInt("correct_count"),
                            rs.getDouble("wrong_rate"));
                    list.add(pair);
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 개념 페어 조회 중 오류 발생");
            e.printStackTrace();
        }

        return list;
    }

    // note_id에 해당하는 기존 페어들을 모두 삭제 후 새로 저장
    public static void replaceAllForNote(int noteId, List<ConceptPair> pairs) {
        String deleteSql = "DELETE FROM concept_pairs WHERE note_id = ?";
        String insertSql = "INSERT INTO concept_pairs (note_id, term, explanation, sort_order) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            conn.setAutoCommit(false);

            deleteStmt.setInt(1, noteId);
            deleteStmt.executeUpdate();

            int index = 0;
            for (ConceptPair pair : pairs) {
                insertStmt.setInt(1, noteId);
                insertStmt.setString(2, pair.getTerm());
                insertStmt.setString(3, pair.getExplanation());
                insertStmt.setInt(4, index++);
                insertStmt.addBatch();
            }

            insertStmt.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            System.out.println("[DB] 개념 페어 저장 중 오류 발생");
            e.printStackTrace();
        }
    }

    public static void updateResult(int pairId, boolean isCorrect) {
        String selectSql = """
                SELECT total_attempts, correct_count
                FROM concept_pairs
                WHERE id = ?
                """;
        String updateSql = """
                UPDATE concept_pairs
                SET total_attempts = ?, correct_count = ?, wrong_rate = ?
                WHERE id = ?
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            selectStmt.setInt(1, pairId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (!rs.next()) return;

                int totalAttempts = rs.getInt("total_attempts");
                int correctCount = rs.getInt("correct_count");

                int newTotal = totalAttempts + 1;
                int newCorrect = correctCount + (isCorrect ? 1 : 0);
                double wrongRate = newTotal == 0 ? 0.0 : (newTotal - newCorrect) / (double) newTotal;

                updateStmt.setInt(1, newTotal);
                updateStmt.setInt(2, newCorrect);
                updateStmt.setDouble(3, wrongRate);
                updateStmt.setInt(4, pairId);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("[DB] 시험 결과 업데이트 중 오류 발생");
            e.printStackTrace();
        }
    }
}

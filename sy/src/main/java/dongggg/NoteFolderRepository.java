package dongggg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteFolderRepository {

    public static List<Folder> findFoldersByNote(int noteId) {
        List<Folder> list = new ArrayList<>();
        String sql = """
                SELECT f.id, f.name, f.created_at
                FROM note_folders nf
                JOIN folders f ON nf.folder_id = f.id
                WHERE nf.note_id = ?
                ORDER BY datetime(f.created_at) DESC, f.id DESC
                """;

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, noteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Folder(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("created_at")));
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB] 노트-폴더 조회 중 오류 발생");
            e.printStackTrace();
        }

        return list;
    }

    public static java.util.Map<Integer, Integer> getFolderNoteCounts() {
        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();
        String sql = "SELECT folder_id, COUNT(*) AS cnt FROM note_folders GROUP BY folder_id";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getInt("folder_id"), rs.getInt("cnt"));
            }

        } catch (SQLException e) {
            System.out.println("[DB] 폴더별 노트 수 조회 중 오류 발생");
            e.printStackTrace();
        }

        return map;
    }

    public static void setNoteFolder(int noteId, int folderId) {
        String deleteSql = "DELETE FROM note_folders WHERE note_id = ?";
        String insertSql = "INSERT INTO note_folders (note_id, folder_id) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            conn.setAutoCommit(false);

            deleteStmt.setInt(1, noteId);
            deleteStmt.executeUpdate();

            insertStmt.setInt(1, noteId);
            insertStmt.setInt(2, folderId);
            insertStmt.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            System.out.println("[DB] 노트 폴더 설정 중 오류 발생");
            e.printStackTrace();
        }
    }
}

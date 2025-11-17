package dongggg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteRepository {

    // 최근 노트 N개 가져오기 (updated_at 기준 내림차순)
    public static List<Note> findRecent(int limit) {
        List<Note> notes = new ArrayList<>();

        String sql = "SELECT id, title, content, created_at, updated_at " +
                "FROM notes " +
                "ORDER BY datetime(updated_at) DESC " +
                "LIMIT ?";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("created_at"),
                            rs.getString("updated_at"));
                    notes.add(note);
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 최근 노트 조회 중 오류 발생");
            e.printStackTrace();
        }

        return notes;
    }

    // 새 노트 INSERT
    public static void insert(Note note) {
        String sql = "INSERT INTO notes (title, content) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[DB] 노트 저장 중 오류 발생");
            e.printStackTrace();
        }
    }

    // 노트가 하나도 없으면 샘플 노트 하나 만들어 넣기
    public static void ensureSampleData() {
        String countSql = "SELECT COUNT(*) FROM notes";

        try (Connection conn = Database.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)) {

            int count = rs.next() ? rs.getInt(1) : 0;

            if (count == 0) {
                System.out.println("[DB] 노트가 없어 샘플 노트를 생성합니다.");
                insert(new Note(
                        "동그리 노트 시작하기",
                        "이곳은 동그리 노트 메모장입니다.\n" +
                                "왼쪽에는 폴더, 아래에는 최근 노트가 표시되고\n" +
                                "나중에는 상세 화면에서 내용을 편집할 수 있게 만들 거예요 :)"));
            }

        } catch (SQLException e) {
            System.out.println("[DB] 샘플 데이터 확인 중 오류 발생");
            e.printStackTrace();
        }
    }
}
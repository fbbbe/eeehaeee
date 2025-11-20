package dongggg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteRepository {

    public record NoteStats(int totalCount, int conceptCount, int normalCount) {
    }

    // 최근 노트 N개 가져오기 (updated_at 기준 내림차순)
    public static List<Note> findRecent(int limit) {
        List<Note> notes = new ArrayList<>();

        String sql = """
                SELECT id, title, content, created_at, updated_at, type
                FROM notes
                ORDER BY datetime(updated_at) DESC
                LIMIT ?
                """;

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
                            rs.getString("updated_at"),
                            rs.getString("type"));
                    notes.add(note);
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 최근 노트 조회 중 오류 발생");
            e.printStackTrace();
        }

        return notes;
    }

    public static List<Note> findByType(String type, int limit) {
        List<Note> notes = new ArrayList<>();

        String sql = """
                SELECT id, title, content, created_at, updated_at, type
                FROM notes
                WHERE type = ?
                ORDER BY datetime(updated_at) DESC
                LIMIT ?
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, type);
            pstmt.setInt(2, limit);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("created_at"),
                            rs.getString("updated_at"),
                            rs.getString("type"));
                    notes.add(note);
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 노트 조회 중 오류 발생 (type)");
            e.printStackTrace();
        }

        return notes;
    }

    // 새 노트 INSERT (id 세팅까지 해줌)
    public static void insert(Note note) {
        String sql = "INSERT INTO notes (title, content, type) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setString(3, note.getType());

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        note.setId(keys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 노트 저장 중 오류 발생");
            e.printStackTrace();
        }
    }

    // 노트 내용 수정 (제목, 내용)
    public static void update(Note note) {
        String sql = "UPDATE notes " +
                "SET title = ?, content = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getContent());
            pstmt.setInt(3, note.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("[DB] 노트 수정 중 오류 발생");
            e.printStackTrace();
        }
    }

    // 노트 삭제 + 관련 개념 페어 삭제
    public static void delete(int id) {
        String deletePairsSql = "DELETE FROM concept_pairs WHERE note_id = ?";
        String deleteNoteSql = "DELETE FROM notes WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement deletePairs = conn.prepareStatement(deletePairsSql);
             PreparedStatement deleteNote = conn.prepareStatement(deleteNoteSql)) {

            conn.setAutoCommit(false);

            deletePairs.setInt(1, id);
            deletePairs.executeUpdate();

            deleteNote.setInt(1, id);
            deleteNote.executeUpdate();

            conn.commit();

        } catch (SQLException e) {
            System.out.println("[DB] 노트 삭제 중 오류 발생");
            e.printStackTrace();
        }
    }

    public static NoteStats getNoteStats() {
        String sql = """
                SELECT COUNT(*) AS total_count,
                       SUM(CASE WHEN type = 'CONCEPT' THEN 1 ELSE 0 END) AS concept_count,
                       SUM(CASE WHEN type = 'NORMAL' THEN 1 ELSE 0 END) AS normal_count
                FROM notes
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return new NoteStats(
                        rs.getInt("total_count"),
                        rs.getInt("concept_count"),
                        rs.getInt("normal_count"));
            }

        } catch (SQLException e) {
            System.out.println("[DB] 노트 통계 조회 중 오류 발생");
            e.printStackTrace();
        }

        return new NoteStats(0, 0, 0);
    }

    // 노트가 하나도 없으면 샘플 노트 하나 만들어 넣기
    public static void ensureSampleData() {
        try (Connection conn = Database.getConnection()) {

            // notes 테이블에 아무 것도 없으면
            String countSql = "SELECT COUNT(*) FROM notes";
            PreparedStatement countStmt = conn.prepareStatement(countSql);
            ResultSet rs = countStmt.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("[DB] 노트가 없어 샘플 개념노트를 생성합니다.");

                String insertNoteSql =
                        "INSERT INTO notes (title, content, type, created_at, updated_at) " +
                                "VALUES (?, ?, ?, datetime('now'), datetime('now'))";

                PreparedStatement insertNote = conn.prepareStatement(insertNoteSql, Statement.RETURN_GENERATED_KEYS);

                insertNote.setString(1, "자바 기본 개념 정리");
                insertNote.setString(2, "기본 문법 정리 및 핵심 개념");
                insertNote.setString(3, "CONCEPT");
                insertNote.executeUpdate();

                ResultSet key = insertNote.getGeneratedKeys();
                int noteId = 0;
                if (key.next()) {
                    noteId = key.getInt(1);
                }

                String insertPairSql =
                        "INSERT INTO concept_pairs (note_id, question, answer) VALUES (?, ?, ?)";

                PreparedStatement insertPair = conn.prepareStatement(insertPairSql);

                insertPair.setInt(1, noteId);
                insertPair.setString(2, "JVM이란?");
                insertPair.setString(3, "자바 프로그램을 실행하는 가상머신");
                insertPair.executeUpdate();

                insertPair.setInt(1, noteId);
                insertPair.setString(2, "클래스와 객체의 차이?");
                insertPair.setString(3, "클래스 = 설계도 / 객체 = 실제 생성된 인스턴스");
                insertPair.executeUpdate();

                insertPair.setInt(1, noteId);
                insertPair.setString(2, "오버라이딩이란?");
                insertPair.setString(3, "상속받은 메서드를 재정의하는 것");
                insertPair.executeUpdate();

                System.out.println("[DB] 샘플 개념노트 + 문제 3개 생성 완료!");
            }

        } catch (SQLException e) {
            System.out.println("[DB] 샘플 데이터 생성 중 오류 발생");
            e.printStackTrace();
        }
    }



    /*───────────────────────────────────────────
     🔍 검색 기능 추가 (부분 일치 검색)
    ───────────────────────────────────────────*/

    public static List<Note> search(String keyword) {
        List<Note> notes = new ArrayList<>();

        String sql = """
                SELECT id, title, content, created_at, updated_at, type
                FROM notes
                WHERE title LIKE ?
                   OR content LIKE ?
                ORDER BY datetime(updated_at) DESC
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String like = "%" + keyword + "%";
            pstmt.setString(1, like);
            pstmt.setString(2, like);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("created_at"),
                            rs.getString("updated_at"),
                            rs.getString("type"));
                    notes.add(note);
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 검색 중 오류 발생");
            e.printStackTrace();
        }

        return notes;
    }

}

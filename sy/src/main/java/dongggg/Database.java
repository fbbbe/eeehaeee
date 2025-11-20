package dongggg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:donggri.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void init() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // notes 테이블: type 추가 (NORMAL / CONCEPT)
            String createNotesTable = """
                    CREATE TABLE IF NOT EXISTS notes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        content TEXT,
                        type TEXT NOT NULL DEFAULT 'NORMAL',
                        created_at TEXT DEFAULT (datetime('now')),
                        updated_at TEXT DEFAULT (datetime('now'))
                    );
                    """;

            /*
             * • id : 기본키
             * • title : 노트 제목
             * • content : 노트 내용
             * • created_at, updated_at : 생성/수정 시간 (일단 TEXT로 둠)
             */

            stmt.execute(createNotesTable);

            // 개념-설명 페어 테이블
            String createConceptPairs = """
                    CREATE TABLE IF NOT EXISTS concept_pairs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        note_id INTEGER NOT NULL,
                        term TEXT NOT NULL,
                        explanation TEXT,
                        sort_order INTEGER DEFAULT 0,
                        total_attempts INTEGER NOT NULL DEFAULT 0,  -- 시험 응시 횟수
                        correct_count INTEGER NOT NULL DEFAULT 0,   -- 맞춘 문제 수
                        wrong_rate REAL NOT NULL DEFAULT 0.0        -- 오답률 (0~1 실수값)
                    );
                    """;

            stmt.execute(createConceptPairs);

            ensureConceptPairColumns(conn);

            // 동그리 레벨업 테이블
            String createDonggriLevels = """
                    CREATE TABLE IF NOT EXISTS donggri_levels (
                        level INTEGER PRIMARY KEY,
                        required_cumulative_score INTEGER NOT NULL,
                        required_cumulative_correct INTEGER NOT NULL
                    );
                    """;
            stmt.execute(createDonggriLevels);
            populateDonggriLevels(conn, 100);

            String createDonggriStatus = """
                    CREATE TABLE IF NOT EXISTS donggri_status (
                        id INTEGER PRIMARY KEY CHECK (id = 1),
                        cumulative_score INTEGER NOT NULL DEFAULT 0,
                        cumulative_correct INTEGER NOT NULL DEFAULT 0
                    );
                    """;
            stmt.execute(createDonggriStatus);
            ensureDonggriStatusRow(conn);

            String createFolders = """
                    CREATE TABLE IF NOT EXISTS folders (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE,
                        created_at TEXT DEFAULT (datetime('now'))
                    );
                    """;
            stmt.execute(createFolders);

            /*
             * level: 레벨
             * required_cumulative_socre: 누적 점수
             * required_cumulative_correct: 누적 맞은 문제 개수
             */

            System.out.println("[DB] notes / concept_pairs / donggri_levels / donggri_status / folders 테이블 초기화 완료");
        } catch (SQLException e) {
            System.out.println("[DB] 초기화 중 오류 발생");
            e.printStackTrace();
        }
    }

    /**
     * 기존 DB에 새 칼럼이 없다면 추가한다.
     */
    private static void ensureConceptPairColumns(Connection conn) throws SQLException {
        ensureColumnExists(conn, "concept_pairs", "total_attempts", "INTEGER NOT NULL DEFAULT 0");
        ensureColumnExists(conn, "concept_pairs", "correct_count", "INTEGER NOT NULL DEFAULT 0");
        ensureColumnExists(conn, "concept_pairs", "wrong_rate", "REAL NOT NULL DEFAULT 0.0");
    }

    /**
     * PRAGMA table_info를 통해 칼럼 존재 여부를 확인한 뒤 없으면 ALTER TABLE 실행.
     */
    private static void ensureColumnExists(Connection conn, String tableName, String columnName,
            String columnDefinition)
            throws SQLException {

        String checkSql = "PRAGMA table_info(" + tableName + ")";
        try (PreparedStatement pstmt = conn.prepareStatement(checkSql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                if (columnName.equalsIgnoreCase(rs.getString("name"))) {
                    return; // 이미 존재
                }
            }
        }

        String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnDefinition;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(alterSql);
        }
    }

    /**
     * donggri_status에 단일 행이 없으면 기본값(0,0)으로 채운다.
     */
    private static void ensureDonggriStatusRow(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM donggri_status WHERE id = 1";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        String insertSql = "INSERT INTO donggri_status (id, cumulative_score, cumulative_correct) VALUES (1, 0, 0)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(insertSql);
        }
    }

    /**
     * 동그리 레벨업 기준 데이터를 채워 넣는다.
     * 레벨 2부터 maxLevel까지 누적 점수/정답 수가 30 / 5 단위로 증가하도록 계산해 삽입.
     */
    private static void populateDonggriLevels(Connection conn, int maxLevel) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM donggri_levels";
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // 이미 데이터가 있으면 보존
            }
        }

        String insertSql = """
                INSERT INTO donggri_levels (level, required_cumulative_score, required_cumulative_correct)
                VALUES (?, ?, ?)
                """;

        int cumulativeScore = 0;
        int cumulativeCorrect = 0;
        int scoreIncrement = 100;
        int correctIncrement = 10;

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (int level = 2; level <= maxLevel; level++) {
                cumulativeScore += scoreIncrement;
                cumulativeCorrect += correctIncrement;

                pstmt.setInt(1, level);
                pstmt.setInt(2, cumulativeScore);
                pstmt.setInt(3, cumulativeCorrect);
                pstmt.addBatch();

                scoreIncrement += 30;
                correctIncrement += 5;
            }
            pstmt.executeBatch();
        }
    }
}

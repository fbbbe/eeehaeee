package dongggg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FolderRepository {

    public static List<Folder> findAll() {
        List<Folder> list = new ArrayList<>();
        String sql = """
                SELECT id, name, created_at
                FROM folders
                ORDER BY datetime(created_at) DESC, id DESC
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Folder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("created_at")));
            }

        } catch (SQLException e) {
            System.out.println("[DB] 폴더 조회 중 오류 발생");
            e.printStackTrace();
        }

        return list;
    }

    /** 🔍 폴더 이름 LIKE 검색 */
    public static List<Folder> search(String keyword) {
        List<Folder> list = new ArrayList<>();

        String sql = """
                SELECT id, name, created_at
                FROM folders
                WHERE name LIKE ?
                ORDER BY datetime(created_at) DESC
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Folder(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("created_at")));
            }

        } catch (SQLException e) {
            System.out.println("[DB] 폴더 검색 중 오류 발생");
            e.printStackTrace();
        }

        return list;
    }

    public static void insert(Folder folder) {
        String sql = "INSERT INTO folders (name) VALUES (?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, folder.getName());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        folder.setId(keys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("[DB] 폴더 저장 중 오류 발생");
            e.printStackTrace();
        }
    }
}

package com.donggri.dao;

import com.donggri.db.ConnectionManager;
import com.donggri.model.Folder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FolderDao {

    public int create(String name) throws SQLException {
        String sql = "INSERT INTO FOLDER(name) VALUES(?)";
        try (Connection con = ConnectionManager.get();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1;
            }
        }
    }

    public List<Folder> findAll() throws SQLException {
        String sql = "SELECT folder_id, name, created_at FROM FOLDER ORDER BY folder_id";
        try (Connection con = ConnectionManager.get();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Folder> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Folder(
                        rs.getInt("folder_id"),
                        rs.getString("name"),
                        rs.getString("created_at")));
            }
            return list;
        }
    }

    public Folder findById(int id) throws SQLException {
        String sql = "SELECT folder_id, name, created_at FROM FOLDER WHERE folder_id = ?";
        try (Connection con = ConnectionManager.get();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Folder(
                            rs.getInt("folder_id"),
                            rs.getString("name"),
                            rs.getString("created_at"));
                }
                return null;
            }
        }
    }

    public int rename(int id, String newName) throws SQLException {
        String sql = "UPDATE FOLDER SET name = ? WHERE folder_id = ?";
        try (Connection con = ConnectionManager.get();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, id);
            return ps.executeUpdate(); // 1이면 성공
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM FOLDER WHERE folder_id = ?";
        try (Connection con = ConnectionManager.get();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
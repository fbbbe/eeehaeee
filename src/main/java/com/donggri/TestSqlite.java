package com.donggri;

import java.sql.*;

public class TestSqlite {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:./donggri.db";
        try (Connection con = DriverManager.getConnection(url)) {
            System.out.println("SQLite 연결 OK");
            try (Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name")) {
                while (rs.next())
                    System.out.println("TABLE: " + rs.getString(1));
            }
        }
    }
}
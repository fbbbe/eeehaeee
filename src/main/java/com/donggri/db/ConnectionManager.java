package com.donggri.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL = "jdbc:sqlite:./donggri.db"; // 프로젝트 루트의 DB 파일

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
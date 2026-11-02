package edu.spendsense.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/spendsense_db?useSSL=false&serverTimezone=Asia/Kolkata";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}

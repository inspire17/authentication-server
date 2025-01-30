package com.inspire17.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PsqlConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/<dbname>";
        String user = "postgres";
        String password = "***";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to the PostgreSQL server successfully.");
            } else {
                System.out.println("Failed to connect to PostgreSQL.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

package atmproject;

import java.sql.*;

public class DBUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/atmdb";
    private static final String USER = "atmdb"; // Replace with your PostgreSQL username
    private static final String PASSWORD = "radha"; // Replace with your PostgreSQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

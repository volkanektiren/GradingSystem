package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String connStr = "jdbc:mysql://localhost:3306/gradingsystem";
    private static Connection conn = null;
    private static String user = "root";
    private static String password = "";

    public static Connection dbConnect() throws SQLException, ClassNotFoundException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Driver");
            e.printStackTrace();
            throw e;
        }

        try {
            conn = DriverManager.getConnection(connStr, user, password);

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console" + e);
            e.printStackTrace();
            throw e;
        }

        return  conn;
    }

    public static void dbDisconnect(Connection conn) {

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

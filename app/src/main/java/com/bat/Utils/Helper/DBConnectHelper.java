package Utils.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Utils.Statics.Const;

public class DBConnectHelper {
    private Connection conn;

    public DBConnectHelper() {
        try {
            Class.forName(Const.DBDRIVER); // often optional with modern drivers
            connect();
            if (conn != null) {
                System.out.println("Connection established successfully.");
                closeConnection();
            } else {
                System.out.println("Failed to establish connection.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        if (conn == null) {
            try {
                String url = Const.DBURL + Const.DBNAME;
                conn = DriverManager.getConnection(url, Const.DBUSERNAME, Const.DBPASSWORD);
            } catch (SQLException e) {
                throw new RuntimeException("SQL error: " + e.getMessage(), e);
            }
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                throw new RuntimeException("Error closing connection: " + e.getMessage(), e);
            }
        }
    }
}
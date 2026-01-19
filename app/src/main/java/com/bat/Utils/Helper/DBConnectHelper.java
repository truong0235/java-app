package Utils.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import Utils.Statics.Const;


public class DBConnectHelper {
    private static Connection conn;
    
    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName(Const.DBDRIVER);
                conn = DriverManager.getConnection(
                        "jdbc:" + Const.DBURL + Const.DBNAME,
                        Const.DBUSERNAME,
                        Const.DBPASSWORD);
            } catch (ClassNotFoundException e) {
                System.out.println("Driver class not found: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("SQL error: " + e.getMessage());
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Class.forName("com.mysql.cj.jdbc.Driver"); 
        Connection connection = DBConnectHelper.getConnection();
        if (connection != null) {
            System.out.println("Connection established successfully.");
            DBConnectHelper.closeConnection();
        } else {
            System.out.println("Failed to establish connection.");
        }
    }
}


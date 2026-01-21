package com.bat.Utils.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.bat.Utils.Statics.Const;

public class DBConnectHelper {

    private Connection conn;

    public DBConnectHelper() throws Exception {
        try {
            Class.forName(Const.DBDRIVER); 
            connect();
        } catch (Exception e) {
            throw new Exception("Cannot established connectiont to database: " + e.getMessage(), e);
        }
    }

    public void connect() throws Exception {
        if (conn == null) {
            try {
                String url = Const.DBURL + Const.DBNAME;
                conn = DriverManager.getConnection(url, Const.DBUSERNAME, Const.DBPASSWORD);
            } catch (SQLException e) {
                throw new RuntimeException("SQL error: " + e.getMessage(), e);
            }
        } else {
            System.out.println("Database connection already exist.");
        }
    }

    public void closeConnection() throws Exception {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                throw new RuntimeException("Error closing connection: " + e.getMessage(), e);
            }
        }
    }

    public static void main(String[] args) {
        try {
            DBConnectHelper dbHelper = new DBConnectHelper();
            System.out.println("Database connection established successfully.");
            dbHelper.closeConnection();
            System.out.println("Database connection closed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.bat.utils.Helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bat.utils.statics.Config;

public class DBConnectHelper {

    private Connection conn;

    public DBConnectHelper() throws Exception {
        try {
            Class.forName(Config.DBDRIVER); 
            connect();
        } catch (Exception e) {
            throw new Exception("Cannot established connectiont to database: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        return conn;
    }
    public void connect() throws Exception {
        if (conn == null) {
            try {
                String url = Config.DBURL + Config.DBNAME;
                conn = DriverManager.getConnection(url, Config.DBUSERNAME, Config.DBPASSWORD);
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
            DBConnectHelper dbhelper = new DBConnectHelper();
            System.out.println("Database connection established successfully.");
            dbhelper.closeConnection();
            System.out.println("Database connection closed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
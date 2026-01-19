package com.bat;


import Utils.Helper.DBConnectHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Explicitly register the driver
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://cheemsserver:3306/batdb",
                    "root",
                    "rootpassword");
            System.out.println("It worked!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver class not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }
}

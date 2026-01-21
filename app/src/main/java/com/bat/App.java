package com.bat;

import Utils.Helper.*;

public class App {
    public static void main(String[] args) {
        try {
            DBConnectHelper conn = new DBConnectHelper();
            System.out.println("It worked!");
        } catch (Exception e) {
            System.out.println("SQL error: " + e.getMessage());
        }
    }
}

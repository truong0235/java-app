/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectHelper {
    // --- CẤU HÌNH DATABASE (Sửa trực tiếp ở đây cho nhanh) ---
    private final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String DB_NAME = "khosach"; // Tên Database của bạn
    private final String DB_URL = "jdbc:mysql://localhost:3306/" + DB_NAME + "?useUnicode=true&characterEncoding=UTF-8";
    private final String USERNAME = "root"; // Tên đăng nhập (thường là root)
    private final String PASSWORD = "";     // Mật khẩu (XAMPP thường để trống)
    // ---------------------------------------------------------

    private Connection conn;

    public DBConnectHelper() throws Exception {
        try {
            Class.forName(DB_DRIVER); 
            connect();
        } catch (Exception e) {
            throw new Exception("Không thể kết nối đến Database: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public void connect() throws Exception {
        if (conn == null || conn.isClosed()) {
            try {
                conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            } catch (SQLException e) {
                throw new RuntimeException("Lỗi SQL: " + e.getMessage(), e);
            }
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm main để test kết nối ngay lập tức (Chạy file này -> Run File)
    public static void main(String[] args) {
        try {
            System.out.println("Đang thử kết nối...");
            DBConnectHelper db = new DBConnectHelper();
            if(db.getConnection() != null) {
                System.out.println("✅ Kết nối thành công tới: " + db.DB_NAME);
            }
            db.closeConnection();
        } catch (Exception e) {
            System.err.println("❌ Kết nối thất bại!");
            e.printStackTrace();
        }
    }
}

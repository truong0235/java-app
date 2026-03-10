package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.UserDTO;
import com.bat.utils.helper.DBConnectHelper;

public class UserDAL {
    
    // Lấy danh sách user đang hoạt động
    public ArrayList<UserDTO> getUsers() {
        ArrayList<UserDTO> users = new ArrayList<>();
        // Chỉ lấy user có status = 1 (Hoạt động)
        String query = "SELECT * FROM users WHERE status = 1"; 
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public UserDTO checkLogin(String username, String password) {
        UserDTO user = null;
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 1";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery(); 
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public int addUser(UserDTO u) {
        int result = 0;
        // Nếu trùng username thì cập nhật lại toàn bộ thông tin mới và set status = 1
        String query = "INSERT INTO users (username, password, fullname, role_id, email, phone, address, status, avatar) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                       "ON DUPLICATE KEY UPDATE " +
                       "password = VALUES(password), fullname = VALUES(fullname), " +
                       "role_id = VALUES(role_id), email = VALUES(email), " +
                       "phone = VALUES(phone), address = VALUES(address), " +
                       "status = 1, avatar = VALUES(avatar)";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getFullName());
            ps.setInt(4, u.getRoleId());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getPhone());
            ps.setString(7, u.getAddress());
            ps.setInt(8, 1); // Trạng thái mặc định khi thêm mới / khôi phục là 1
            ps.setString(9, u.getAvatar());
            
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int updateUser(UserDTO u) {
        int result = 0;
        // Không update password và username ở đây để bảo mật, chỉ update thông tin
        String query = "UPDATE users SET fullname=?, role_id=?, email=?, phone=?, address=?, status=?, avatar=? WHERE user_id=?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setString(1, u.getFullName());
            ps.setInt(2, u.getRoleId());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getAddress());
            ps.setInt(6, u.getStatus());
            ps.setString(7, u.getAvatar());
            ps.setInt(8, u.getUserId());
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int deleteUser(int userId) {
        int result = 0;
        // Xóa mềm: Chuyển status về 0 chứ không xóa hẳn database
        String query = "UPDATE users SET status = 0 WHERE user_id = ?"; 
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, userId);
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // ==========================================
    // 🔐 HÀM KHÔI PHỤC MẬT KHẨU (XÁC MINH 3 LỚP)
    // ==========================================
    public int resetPassword(String username, String phone, String email, String newPassword) {
        int result = 0;
        // Cập nhật mật khẩu nếu khớp cả username, phone VÀ email (chỉ cho user đang hoạt động)
        String query = "UPDATE users SET password = ? WHERE username = ? AND phone = ? AND email = ? AND status = 1"; 
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            ps.setString(3, phone);
            ps.setString(4, email);
            
            result = ps.executeUpdate(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Hàm phụ trợ map dữ liệu
    private UserDTO mapResultSetToUser(ResultSet rs) throws Exception {
        return new UserDTO(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("fullname"),
            rs.getInt("role_id"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("address"),
            rs.getInt("status"),
            rs.getString("avatar")
        );
    }
}

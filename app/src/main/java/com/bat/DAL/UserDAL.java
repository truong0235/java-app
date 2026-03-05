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
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ) {
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
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
            db.closeConnection();
    public UserDTO getUserById(int userId) {
        UserDTO user = null;
        String query = "SELECT * FROM users WHERE user_id = ? AND status = 1";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new UserDTO(
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}

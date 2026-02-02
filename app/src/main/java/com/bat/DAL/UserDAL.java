package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.UserDTO;
import com.bat.utils.helper.DBConnectHelper;

public class UserDAL {
    public ArrayList<UserDTO> getUsers(){
        ArrayList<UserDTO> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO(
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
                users.add(user);
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}

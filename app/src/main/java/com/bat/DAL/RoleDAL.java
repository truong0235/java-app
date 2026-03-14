package com.bat.DAL;

import com.bat.DTO.RoleDTO;
import com.bat.utils.helper.DBConnectHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAL {

    private final String TABLE_NAME = "roles";

    public RoleDAL() {
    }

    public List<RoleDTO> getAllRoles() {
        List<RoleDTO> roleList = new ArrayList<>();
        try (DBConnectHelper dbHelper = new DBConnectHelper();
             Connection con = dbHelper.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE status = 1")) {
            while (rs.next()) {
                RoleDTO role = new RoleDTO();
                role.setRole_id(rs.getInt("role_id"));
                role.setName(rs.getString("name"));
                role.setStatus((byte) rs.getInt("status"));
                roleList.add(role);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roleList;
    }

    public RoleDTO getRoleById(int roleId) {
        RoleDTO role = null;
        try (DBConnectHelper dbHelper = new DBConnectHelper();
             Connection con = dbHelper.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE role_id = ? AND status = 1")) {
            pst.setInt(1, roleId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    role = new RoleDTO();
                    role.setRole_id(rs.getInt("role_id"));
                    role.setName(rs.getString("name"));
                    role.setStatus((byte) rs.getInt("status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }

    public boolean insertRole(RoleDTO role) {
        try (DBConnectHelper dbHelper = new DBConnectHelper();
             Connection con = dbHelper.getConnection();
             PreparedStatement pst = con.prepareStatement("INSERT INTO " + TABLE_NAME + " (name, status) VALUES (?, ?)")) {
            pst.setString(1, role.getName());
            pst.setInt(2, role.getStatus());
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRole(RoleDTO role) {
        try (DBConnectHelper dbHelper = new DBConnectHelper();
             Connection con = dbHelper.getConnection();
             PreparedStatement pst = con.prepareStatement("UPDATE " + TABLE_NAME + " SET name = ?, status = ? WHERE role_id = ?")) {
            pst.setString(1, role.getName());
            pst.setInt(2, role.getStatus());
            pst.setInt(3, role.getRole_id());
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRole(int roleId) {
        try (DBConnectHelper dbHelper = new DBConnectHelper();
             Connection con = dbHelper.getConnection();
             PreparedStatement pst = con.prepareStatement("UPDATE " + TABLE_NAME + " SET status = 0 WHERE role_id = ?")) {
            pst.setInt(1, roleId);
            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<RoleDTO> searchRoles(String keyword, String type) {
        List<RoleDTO> roleList = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE status = 1";

        if (keyword != null && !keyword.isEmpty()) {
            switch (type) {
                case "Mã vai trò" -> sql += " AND role_id = ?";
                case "Tên vai trò" -> sql += " AND name LIKE ?";
                default -> {
                    // Search all columns for the keyword
                    sql += " AND (role_id LIKE ? OR name LIKE ?)";
                }
            }
        }

        try (DBConnectHelper dbHelper = new DBConnectHelper();
             Connection con = dbHelper.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            if (keyword != null && !keyword.isEmpty()) {
                switch (type) {
                    case "Mã vai trò" -> {
                        try {
                            int id = Integer.parseInt(keyword);
                            pst.setInt(1, id);
                        } catch (NumberFormatException e) {
                            // If keyword is not a valid integer for "Mã vai trò", return empty list
                            return new ArrayList<>();
                        }
                    }
                    case "Tên vai trò" -> pst.setString(1, "%" + keyword + "%");
                    default -> {
                        // For default case where both role_id and name are searched
                        try {
                            int id = Integer.parseInt(keyword);
                            pst.setInt(1, id); // Assuming role_id is integer
                        } catch (NumberFormatException e) {
                            pst.setInt(1, -1); // Use -1 to ensure no match for role_id
                        }
                        pst.setString(2, "%" + keyword + "%");
                    }
                }
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    RoleDTO role = new RoleDTO();
                    role.setRole_id(rs.getInt("role_id"));
                    role.setName(rs.getString("name"));
                    role.setStatus((byte) rs.getInt("status"));
                    roleList.add(role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roleList;
    }
}

package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.bat.DTO.PermissionDTO;
import com.bat.utils.helper.DBConnectHelper;

public class PermDAL {
    public static ArrayList<PermissionDTO> getAllPermsById(int roleId) {
        String query = "SELECT * FROM permission";
        ArrayList<PermissionDTO> perms = new ArrayList<>();
        try (DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, roleId);
            var rs = stmt.executeQuery();
            while (rs.next()) {
                PermissionDTO data = new PermissionDTO();
                data.setPerm_id(rs.getInt("perm_id"));
                data.setResource(rs.getString("resource"));
                data.setPerm_value((byte) rs.getInt("perm_value"));
                perms.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perms;
    }

    // public static void updatePerms(int roleId, ArrayList<PermissionDTO> perms) {
    //     String deleteQuery = "DELETE FROM permission WHERE role_id = ?";
    //     String insertQuery = "INSERT INTO permission (role_id, func_name, perm_value) VALUES (?, ?, ?)";

    //     try (DBConnectHelper db = new DBConnectHelper();
    //         Connection conn = db.getConnection();
    //         PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
    //         PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
    //     ) {
    //         // Xóa hết quyền cũ của role
    //         deleteStmt.setInt(1, roleId);
    //         deleteStmt.executeUpdate();

    //         // Thêm quyền mới
    //         for (PermissionDTO perm : perms) {
    //             insertStmt.setInt(1, roleId);
    //             insertStmt.setString(2, perm.getFunc_name());
    //             insertStmt.setInt(3, perm.getPerm_value());
    //             insertStmt.addBatch();
    //         }
    //         insertStmt.executeBatch();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    public static boolean updatePerm(int permId, int permValue) {
        String query = "UPDATE permission SET perm_value = ? WHERE perm_id = ?";
        try (DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, permValue);
            stmt.setInt(2, permId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean disablePerm(int permId) {
        String query = "UPDATE permission SET status = 0 WHERE perm_id = ?";
        try (DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
        ) {
            stmt.setInt(1, permId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean addPerm(int roleId, String funcName, int permValue) {
        String query = "INSERT INTO permission (role_id, func_name, perm_value) VALUES (?, ?, ?)";
        try (DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, roleId);
            stmt.setString(2, funcName);
            stmt.setInt(3, permValue);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                var generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return true; // Trả về true nếu thêm thành công
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu không thành công
    }



}

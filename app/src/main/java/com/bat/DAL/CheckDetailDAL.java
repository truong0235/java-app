package com.bat.DAL;

import java.util.ArrayList;

import com.bat.DTO.CheckDetailDTO;
import com.bat.Utils.Helper.DBConnectHelper;

public class CheckDetailDAL {
    public ArrayList<CheckDetailDTO> getAllCheckDetails (int checkId){
        ArrayList<CheckDetailDTO> list = new ArrayList<>();
        try {
            DBConnectHelper db = new DBConnectHelper();
            String query = "SELECT * FROM check_detail WHERE check_id = ?";
            java.sql.Connection conn = db.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, checkId);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CheckDetailDTO checkDetail = new CheckDetailDTO(
                    rs.getInt("check_id"),
                    rs.getInt("lot_id"),
                    rs.getInt("differnce"),
                    rs.getInt("actual_quantity"),
                    rs.getInt("system_quantity")
                );
                list.add(checkDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int add (CheckDetailDTO detail){
        String query = "INSERT INTO check_detail (check_id, lot_id, differnce, actual_quantity, system_quantity) VALUES (?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            java.sql.Connection conn = db.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(query, java.sql.PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setInt(1, detail.getCheckId());
            ps.setInt(2, detail.getLotId());
            ps.setInt(3, detail.getDifference());
            ps.setInt(4, detail.getActualQuantity());
            ps.setInt(5, detail.getSystemQuantity());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (java.sql.ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); 
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean delete(int checkId){
        // cân nhắc xóa mềm 
        String query = "DELETE FROM check_detail WHERE check_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            java.sql.Connection conn = db.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, checkId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.InventoryCheckDTO;
import com.bat.Utils.Helper.DBConnectHelper;

public class InventoryCheckDAL {
    public ArrayList<InventoryCheckDTO> getInventoryChecks() {
        ArrayList<InventoryCheckDTO> inventoryChecks = new ArrayList<>();
        String query = "SELECT check_id, user_id, check_date, status FROM InventoryChecks ORDER BY check_date DESC";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = (ResultSet) ps.executeQuery();
            while (rs.next()) {
                InventoryCheckDTO check = new InventoryCheckDTO(
                    rs.getInt("check_id"),
                    rs.getTimestamp("check_date").toLocalDateTime(),
                    rs.getInt("user_id"),
                    rs.getInt("status")
                );
                inventoryChecks.add(check);
            }
            db.closeConnection();
            return inventoryChecks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public int add(InventoryCheckDTO check){
        String query = "INSERT INTO InventoryChecks (user_id, status) VALUES (?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setInt(1, check.getUserId());
            ps.setInt(2, check.getStatus());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0){
                try (ResultSet rs = ps.getGeneratedKeys()){
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

    public boolean delete(int checkId) { // update SL sp, lô -> xóa ct kiểm kê, lsu thay đổi, 
        CheckDetailDAL checkDetailDAL = new CheckDetailDAL();
        LotTransactionDAL lotTransactionDAL = new LotTransactionDAL();

        ArrayList<CheckDetailDTO> details = checkDetailDAL.getCheckDetails(checkId);
        for (CheckDetailDTO detail : details) {
            int id = detail.getLotId();
            LotDAL lotDAL = new LotDAL();
            ProductDAL productDAL = new ProductDAL();

            lotDAL.updateQuantity(id, - detail.getDifference());

            productDAL.updateQuantityByLotId(id, - detail.getDifference());
        }

        checkDetailDAL.delete(checkId);
        lotTransactionDAL.delete(checkId);
        
        String query = "UPDATE inventory_check SET status = 0 WHERE check_id = ?";
        try{
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, checkId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
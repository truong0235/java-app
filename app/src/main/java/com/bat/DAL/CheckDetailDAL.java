package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.LotTransactionDTO;
import com.bat.utils.helper.DBConnectHelper;

public class CheckDetailDAL {
    public ArrayList<CheckDetailDTO> getCheckDetails (int checkId){
        ArrayList<CheckDetailDTO> list = new ArrayList<>();
        try {
            DBConnectHelper db = new DBConnectHelper();
            String query = "SELECT * FROM check_detail WHERE check_id = ?";
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, checkId);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CheckDetailDTO checkDetail = new CheckDetailDTO(
                    rs.getInt("check_id"),
                    rs.getInt("lot_id"),
                    rs.getInt("difference"),
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

    public boolean add (CheckDetailDTO detail){
        String query = "INSERT INTO check_detail (check_id, lot_id, difference, actual_quantity, system_quantity) VALUES (?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setInt(1, detail.getCheckId());
            ps.setInt(2, detail.getLotId());
            ps.setInt(3, detail.getDifference());
            ps.setInt(4, detail.getActualQuantity());
            ps.setInt(5, detail.getSystemQuantity());

            if (ps.executeUpdate() > 0){
                LotTransactionDTO lt= new LotTransactionDTO(
                    0,
                    detail.getLotId(),
                    detail.getCheckId(),
                    detail.getActualQuantity() - detail.getSystemQuantity(),
                    detail.getActualQuantity(),
                    java.time.LocalDateTime.now(),
                    "adjust"
                );

                LotTransactionDAL lotTransactionDAL = new LotTransactionDAL();
                LotDAL lotDAL = new LotDAL();
                ProductDAL productDAL = new ProductDAL();

                boolean trResult = lotTransactionDAL.add(lt) != -1;
                int lotId = detail.getLotId();
                boolean lResult = lotDAL.updateQuantity(lotId, detail.getDifference()); 

                boolean pResult = productDAL.updateQuantityByLotId(lotId, detail.getDifference());
                return trResult && lResult && pResult;   
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int checkId){
        // cân nhắc xóa mềm 
        String query = "DELETE FROM check_detail WHERE check_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, checkId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
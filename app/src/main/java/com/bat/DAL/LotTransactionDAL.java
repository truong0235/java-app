package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.bat.DTO.LotTransactionDTO;
import com.bat.utils.helper.DBConnectHelper;

public class LotTransactionDAL {
    public ArrayList<LotTransactionDTO> getLotTransactionsByLotId(int lotId) {
       ArrayList<LotTransactionDTO> transList = new ArrayList<>();
       String query = "SELECT trans_id, lot_id, ref_id, quantity_change, quantity, date, type FROM lot_transaction WHERE lot_id = ?";
       try {   
           DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, lotId);
            ResultSet rs = (ResultSet) ps.executeQuery();
            while (rs.next()) {
                LotTransactionDTO trans = new LotTransactionDTO(
                    rs.getInt("trans_id"),
                    rs.getInt("lot_id"),
                    rs.getInt("ref_id"),
                    rs.getInt("quantity_change"),
                    rs.getInt("quantity"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getString("type")
                );
                transList.add(trans);
            }
            db.closeConnection();
       }
       catch (Exception e) {
              e.printStackTrace();
       }
       return transList;
    }

    public int add(LotTransactionDTO tx){
        String query = "INSERT INTO lot_transaction (lot_id, ref_id, quantity_change, quantity, date, type) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, tx.getLotId());
            ps.setInt(2, tx.getRefId());
            ps.setInt(3, tx.getQuantityChange());
            ps.setInt(4, tx.getQuantity());
            ps.setTimestamp(5, Timestamp.valueOf(tx.getTransDate()));
            ps.setString(6, tx.getTransType());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
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

    public boolean delete(int refId) {
        String query = "DELETE FROM lot_transaction WHERE ref_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, refId);
            int affectedRows = ps.executeUpdate();
            db.closeConnection();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

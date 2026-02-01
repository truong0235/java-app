package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;
import com.bat.utils.helper.DBConnectHelper;

public class ImportDAL {
    public ArrayList<ImportDTO> getImports() {
        ArrayList<ImportDTO> importList = new ArrayList<>();
        try {
            DBConnectHelper db = new DBConnectHelper();
            String query = "SELECT import_id, import_date, status, total_price, provider_id, user_id" +
                           "FROM import_receipt ORDER BY import_date DESC";
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = (ResultSet) ps.executeQuery();
            while (rs.next()) {
                ImportDTO imp = new ImportDTO(
                    rs.getInt("import_id"),
                    rs.getInt("user_id"),
                    rs.getBigDecimal("total_price"),
                    rs.getInt("status"),
                    rs.getTimestamp("import_date").toLocalDateTime(),
                    rs.getInt("provider_id")
                );
                importList.add(imp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return importList;
    }

    public int add(ImportDTO imp){
        String query = "INSERT INTO import_receipt (user_id, total_price, import_date, provider_id) VALUES (?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setInt(1, imp.getUserId());
            ps.setBigDecimal(2, imp.getTotalPrice());
            ps.setTimestamp(3, Timestamp.valueOf(imp.getCreatedDate()));
            ps.setInt(4, imp.getProviderId());
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

    public boolean update(ImportDTO imp){
        String query = "UPDATE import_receipt SET total_price = ?, import_date = ?, provider_id = ? WHERE import_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);

            ps.setBigDecimal(1, imp.getTotalPrice());
            ps.setTimestamp(2, Timestamp.valueOf(imp.getCreatedDate()));
            ps.setInt(3, imp.getProviderId());
            ps.setInt(4, imp.getReceiptId());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(int importId){ //  kiểm tra trước khi xoá -> update SL sp -> xoá  lịch sử nhập kho, các lot liên quan, import 
        LotTransactionDAL transDAL = new LotTransactionDAL();
        LotDAL lotDAL = new LotDAL();

        ArrayList<LotDTO> lotList = lotDAL.getLotsByImpId(importId);
        for (LotDTO lot : lotList) {
            int lotId = lot.getLotId();
            int prQty = lot.getInitialQuantity();

            ProductDAL productDAL = new ProductDAL();
            productDAL.updateQuantityByLotId(lotId, - prQty);
        }

        lotDAL.deleteByImpId(importId);
        transDAL.delete(importId);


        String query = "UPDATE import_receipt SET status = 0 WHERE import_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, importId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUsedLot (int importId){
        String query = "SELECT COUNT(*) AS count FROM lot WHERE import_id = ? AND quantity != initial_quantity";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, importId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count == 0; 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

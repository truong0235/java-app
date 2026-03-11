package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.bat.DTO.ExportLotDTO;
import com.bat.DTO.LotTransactionDTO;
import com.bat.utils.helper.DBConnectHelper;


public class ExportLotDAL {
    public boolean addExportLot(ExportLotDTO exportLot) {
        String query = "INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES (?, ?, ?, ?, ?)";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
                int lotId = exportLot.getLotId();
                ps.setInt(1, exportLot.getExportId());
                ps.setInt(2, exportLot.getProductId());
                ps.setInt(3, exportLot.getLotId());
                ps.setInt(4, exportLot.getQuantity());
                ps.setBigDecimal(5, exportLot.getExportPrice());

                if (ps.executeUpdate() > 0) {
                    LotDAL lotDAL = new LotDAL();
                    ProductDAL productDAL = new ProductDAL();
                    LotTransactionDAL lotTransactionDAL = new LotTransactionDAL();
                    
                    lotDAL.updateQuantity(lotId, -exportLot.getQuantity());
                    
                    int remmainQty = lotDAL.getLotById(lotId).getQuantity();
                    if (remmainQty == 0) {
                        lotDAL.updateStatus(lotId, "Hết");
                    }
                    productDAL.updateQuantityByLotId(lotId, -exportLot.getQuantity());
                    LotTransactionDTO trans = new LotTransactionDTO(
                        0,
                        lotId,
                        exportLot.getExportId(),
                        -exportLot.getQuantity(),
                        remmainQty,
                        LocalDateTime.now(),
                        "export"
                    );
                    lotTransactionDAL.add(trans);
                    return true;
                }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ExportLotDTO> getExportLotsByExportId(int exportId) {
        ArrayList<ExportLotDTO> list = new ArrayList<>();
        String query = "SELECT el.*, p.product_name, l.lot_code " +
                      "FROM export_lot el " +
                      "JOIN lot l ON el.lot_id = l.lot_id " +
                      "JOIN product p ON el.product_id = p.product_id " +
                      "WHERE el.export_id = ?";
        
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, exportId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ExportLotDTO exportLot = new ExportLotDTO(
                    rs.getInt("export_id"),
                    rs.getInt("product_id"),
                    rs.getInt("lot_id"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("export_price")
                );
                exportLot.setProductName(rs.getString("product_name"));
                exportLot.setLotCode(rs.getString("lot_code"));
                list.add(exportLot);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public boolean deleteByExportId(int exportId) {
        String query = "DELETE FROM export_lot WHERE export_id = ?";
        
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, exportId);
            return ps.executeUpdate() > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

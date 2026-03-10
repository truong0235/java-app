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
    
    /**
     * Thêm chi tiết lô xuất vào bảng export_lot
     * Đồng thời cập nhật:
     * 1. Số lượng lô (giảm)
     * 2. Số lượng sản phẩm (giảm)
     * 3. Thêm lịch sử lot_transaction (type: export)
     * 4. Cập nhật trạng thái lô thành "Hết" nếu xuất hết
     */
    public boolean addExportLot(ExportLotDTO exportLot) {
        String query = "INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES (?, ?, ?, ?, ?)";
        // String updateLotQuantityQuery = "UPDATE lot SET quantity = quantity - ? WHERE lot_id = ?";
        // String updateLotStatusQuery = "UPDATE lot SET status = 'Hết' WHERE lot_id = ? AND quantity = 0";
        // String updateProductQuantityQuery = "UPDATE product SET quantity = quantity - ? WHERE product_id = ?";
        // String insertTransactionQuery = "INSERT INTO lot_transaction (lot_id, ref_id, quantity_change, quantity, date, type) VALUES (?, ?, ?, ?, ?, ?)";
        // String getLotQuantityQuery = "SELECT quantity FROM lot WHERE lot_id = ?";
        
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
                int lotId = exportLot.getLotId();
            // 1. Thêm vào export_lot
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
            
            // 2. Cập nhật số lượng lô (giảm)
            // try (PreparedStatement ps = conn.prepareStatement(updateLotQuantityQuery)) {
            //     ps.setInt(1, exportLot.getQuantity());
            //     ps.setInt(2, lotId);
            //     ps.executeUpdate();
            // }
            
            // 3. Lấy số lượng còn lại của lô sau khi cập nhật
            // int remainingQuantity = 0;
            // try (PreparedStatement ps = conn.prepareStatement(getLotQuantityQuery)) {
            //     ps.setInt(1, lotId);
            //     ResultSet rs = ps.executeQuery();
            //     if (rs.next()) {
            //         remainingQuantity = rs.getInt("quantity");
            //     }
            // }
            
            // 4. Cập nhật trạng thái lô nếu hết hàng
            // if (remainingQuantity == 0) {
            //     try (PreparedStatement ps = conn.prepareStatement(updateLotStatusQuery)) {
            //         ps.setInt(1, lotId);
            //         ps.executeUpdate();
            //     }
            // }
            
            // 5. Cập nhật số lượng sản phẩm (giảm)
            // try (PreparedStatement ps = conn.prepareStatement(updateProductQuantityQuery)) {
            //     ps.setInt(1, exportLot.getQuantity());
            //     ps.setInt(2, exportLot.getProductId());
            //     ps.executeUpdate();
            // }
            
            // 6. Thêm lịch sử lot_transaction
            // try (PreparedStatement ps = conn.prepareStatement(insertTransactionQuery)) {
            //     ps.setInt(1, lotId);
            //     ps.setInt(2, exportId);
            //     ps.setInt(3, -exportLot.getQuantity()); // Số lượng thay đổi (âm vì xuất)
            //     ps.setInt(4, remainingQuantity); // Số lượng còn lại
            //     ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            //     ps.setString(6, "export");
            //     ps.executeUpdate();
            // }
            
            // conn.commit(); // Commit transaction
            // return true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy danh sách lô xuất theo export_id
     */
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

    /**
     * Xóa tất cả lô xuất của một phiếu xuất
     */
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

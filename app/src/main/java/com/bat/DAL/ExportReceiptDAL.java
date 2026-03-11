package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.bat.DTO.ExportLotDTO;
import com.bat.DTO.ExportReceiptDTO;
import com.bat.utils.helper.DBConnectHelper;

public class ExportReceiptDAL {
    
    public ArrayList<ExportReceiptDTO> getExports() {
        ArrayList<ExportReceiptDTO> list = new ArrayList<>();
        String query = "SELECT export_id, export_date, status, user_id, total_price, customer_id FROM export_receipt WHERE status = 1";

        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                ExportReceiptDTO exportReceipt = new ExportReceiptDTO(
                    rs.getInt("export_id"),
                    rs.getTimestamp("export_date").toLocalDateTime(),
                    rs.getInt("status"),
                    rs.getInt("user_id"),
                    rs.getInt("total_price"),
                    rs.getInt("customer_id"),
                    0
                );
                list.add(exportReceipt);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getExports: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error initializing database connection in getAll: " + e.getMessage());
        }
        return list;
    }

    public int add(ExportReceiptDTO exportReceipt) {
        String query = "INSERT INTO export_receipt (export_date, status, user_id, total_price, customer_id) VALUES (?, ?, ?, ?, ?)";
        
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(exportReceipt.getExport_date()));
            ps.setInt(2, exportReceipt.getStatus());
            ps.setInt(3, exportReceipt.getUser_id());
            ps.setInt(4, exportReceipt.getTotal_price());
            ps.setInt(5, exportReceipt.getCustomer_id());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error initializing database connection in getAll: " + e.getMessage());
        }
        return -1;
    }

    public boolean deleteExportDetails(int exportId) {
        String query = "DELETE FROM export_detail WHERE export_id = ?";
        
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, exportId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error initializing database connection in getAll: " + e.getMessage());
        }
        return false;
    }


    public boolean delete(int exportId) {
        LotTransactionDAL lotTransactionDAL = new LotTransactionDAL();
        ExportLotDAL exportLotDAL = new ExportLotDAL();

        ArrayList<ExportLotDTO> exportLots = exportLotDAL.getExportLotsByExportId(exportId);
        for (ExportLotDTO exportLot : exportLots) {
            ProductDAL productDAL = new ProductDAL();
            LotDAL lotDAL = new LotDAL();

            lotDAL.updateQuantity(exportLot.getLotId(), exportLot.getQuantity());
            productDAL.updateQuantityByLotId(exportLot.getLotId(), exportLot.getQuantity());
        }

        exportLotDAL.deleteByExportId(exportId);
        deleteExportDetails(exportId);
        lotTransactionDAL.delete(exportId);

        String query = "UPDATE export_receipt SET status = 0 WHERE export_id = ?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, exportId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error initializing database connection in getAll: " + e.getMessage());
        }
        return false;
    }
}

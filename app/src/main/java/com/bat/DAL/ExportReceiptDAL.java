package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.bat.DTO.ExportReceiptDTO;
import com.bat.utils.helper.DBConnectHelper;

public class ExportReceiptDAL {
    
    /**
     * Lấy tất cả phiếu xuất
     */
    public ArrayList<ExportReceiptDTO> getAll() {
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
                    0 // order_id
                );
                list.add(exportReceipt);
            }
        } catch (SQLException e) {
            System.err.println("Database error in getAll: " + e.getMessage());
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

    /**
     * Cập nhật phiếu xuất
     */
    public boolean update(ExportReceiptDTO exportReceipt) {
        String query = "UPDATE export_receipt SET export_date = ?, status = ?, user_id = ?, total_price = ?, customer_id = ? WHERE export_id = ?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(exportReceipt.getExport_date()));
            ps.setInt(2, exportReceipt.getStatus());
            ps.setInt(3, exportReceipt.getUser_id());
            ps.setInt(4, exportReceipt.getTotal_price());
            ps.setInt(5, exportReceipt.getCustomer_id());
            ps.setInt(6, exportReceipt.getExport_id());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error initializing database connection in getAll: " + e.getMessage());
        }
        return false;
    }

    /**
     * Xóa phiếu xuất (soft delete)
     */
    public boolean delete(int exportId) {
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

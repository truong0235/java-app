package com.bat.DAL;

import com.bat.utils.helper.DBConnectHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.bat.DTO.ExportREceiptDTO;


public class ExportReceiptDAL {
    
    public ArrayList<ExportREceiptDTO> getAll(){
        ArrayList<ExportREceiptDTO> list = new ArrayList<>();
        String query = "SELECT export_id, export_date, status, user_id, total_price, customer_id, order_id FROM export_receipt WHERE status = 1";

        try (Connection conn = new DBConnectHelper().getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ExportREceiptDTO exportReceipt = new ExportREceiptDTO(
                    rs.getInt("export_id"),
                    rs.getTimestamp("export_date").toLocalDateTime(),
                    rs.getInt("status"),
                    rs.getInt("user_id"),
                    rs.getInt("total_price"),
                    rs.getInt("customer_id"),
                    rs.getInt("order_id")
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

    public boolean create(ExportREceiptDTO exportReceipt) {
        String query = "INSERT INTO export_receipt (export_date, status, user_id, total_price, customer_id, order_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBConnectHelper().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(exportReceipt.getExport_date()));
            ps.setInt(2, exportReceipt.getStatus());
            ps.setInt(3, exportReceipt.getUser_id());
            ps.setInt(4, exportReceipt.getTotal_price());
            ps.setInt(5, exportReceipt.getCustomer_id());
            ps.setInt(6, exportReceipt.getOrder_id());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error adding export receipt: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error establishing database connection: " + e.getMessage(), e);
        }
    }

    public boolean update(ExportREceiptDTO exportReceipt) {
        String query = "UPDATE export_receipt SET export_date = ?, status = ?, user_id = ?, total_price = ?, customer_id = ?, order_id = ? WHERE export_id = ?";

        try (Connection conn = new DBConnectHelper().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setTimestamp(1, java.sql.Timestamp.valueOf(exportReceipt.getExport_date()));
            ps.setInt(2, exportReceipt.getStatus());
            ps.setInt(3, exportReceipt.getUser_id());
            ps.setInt(4, exportReceipt.getTotal_price());
            ps.setInt(5, exportReceipt.getCustomer_id());
            ps.setInt(6, exportReceipt.getOrder_id());
            ps.setInt(7, exportReceipt.getExport_id());

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error updating export receipt: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error establishing database connection: " + e.getMessage(), e);
        }
    }

    public boolean delete(int exportId) {
        String query = "UPDATE export_receipt SET status = 0 WHERE export_id = ?";

        try (Connection conn = new DBConnectHelper().getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, exportId);

            int rowsAffected = ps.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting export receipt: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error establishing database connection: " + e.getMessage(), e);
        }
    }

    public boolean delete(ExportREceiptDTO exportReceipt) {
        return delete(exportReceipt.getExport_id());
    }

}

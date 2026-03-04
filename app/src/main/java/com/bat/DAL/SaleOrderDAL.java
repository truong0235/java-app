package com.bat.DAL;
import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bat.DTO.SaleOrderDTO;
import com.bat.utils.helper.DBConnectHelper;

public class SaleOrderDAL {

    
    public ArrayList<String> getSaleOrders() {
        ArrayList<String> saleOrders = new ArrayList<>();
        String query = "SELECT * FROM sale_order WHERE status = 1";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String orderInfo = "Order ID: " + rs.getInt("order_id") +
                        ", Order Date: " + rs.getTimestamp("order_date").toLocalDateTime() +
                        ", Expected Date: " + rs.getTimestamp("expected_date").toLocalDateTime() +
                        ", Status: " + rs.getInt("status") +
                        ", Customer ID: " + rs.getInt("customer_id") +
                        ", User ID: " + rs.getInt("user_id");
                saleOrders.add(orderInfo);
            }
            db.closeConnection();
        } catch (Exception e) {
            System.out.println("Error fetching sale orders: " + e.getMessage());
            e.printStackTrace();
        }
        return saleOrders;
    }

    public boolean add(SaleOrderDTO order) {
        String query = "INSERT INTO sale_order (order_id, order_date, expected_date, status, customer_id, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, order.getOrder_id());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(order.getOrder_date()));
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(order.getExpected_date()));
            ps.setInt(4, order.getStatus());
            ps.setInt(5, order.getCustomer_id());
            ps.setInt(6, order.getUser_id());
            int rowsAffected = ps.executeUpdate();
            db.closeConnection();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Error adding sale order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean update(SaleOrderDTO order) {
        String query = "UPDATE sale_order SET order_date = ?, expected_date = ?, status = ?, customer_id = ?, user_id = ? WHERE order_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(order.getOrder_date()));
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(order.getExpected_date()));
            ps.setInt(3, order.getStatus());
            ps.setInt(4, order.getCustomer_id());
            ps.setInt(5, order.getUser_id());
            ps.setInt(6, order.getOrder_id());
            int rowsAffected = ps.executeUpdate();
            db.closeConnection();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Error updating sale order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int orderId) {
        String query = "UPDATE sale_order SET status = 0 WHERE order_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
            int rowsAffected = ps.executeUpdate();
            db.closeConnection();
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("Error deleting sale order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


}

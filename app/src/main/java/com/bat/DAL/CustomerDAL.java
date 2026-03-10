package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.CustomerDTO;
import com.bat.utils.helper.DBConnectHelper;

public class CustomerDAL {

    public ArrayList<CustomerDTO> getCustomers() {
        ArrayList<CustomerDTO> customers = new ArrayList<>();
        String query = "SELECT * FROM customer WHERE status = 1";
        try (DBConnectHelper db = new DBConnectHelper();
             Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Convert java.sql.Date to java.util.Date
                java.sql.Date sqlDate = rs.getDate("birthday");
                java.util.Date birthday = (sqlDate != null) ? new java.util.Date(sqlDate.getTime()) : null;
                
                CustomerDTO customer = new CustomerDTO(
                        rs.getInt("customer_id"),
                        rs.getString("fullname"),
                        birthday,
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("image")
                );
                customers.add(customer);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return customers;
    }

    public int getAutoIncrement() {
        int nextId = 1;
        String query = "SELECT MAX(customer_id) FROM customer";
        try (DBConnectHelper db = new DBConnectHelper();
             Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) nextId = rs.getInt(1) + 1;
        } catch (Exception e) { e.printStackTrace(); }
        return nextId;
    }

    public boolean add(CustomerDTO c) {
        String query = "INSERT INTO customer (customer_id, fullname, birthday, phone, address, image) VALUES (?, ?, ?, ?, ?, ?)";
        try (DBConnectHelper db = new DBConnectHelper();
             Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, c.getCustomerId());
            ps.setString(2, c.getFullName());
            // Convert java.util.Date to java.sql.Date
            if (c.getBirthday() != null) {
                ps.setDate(3, new java.sql.Date(c.getBirthday().getTime()));
            } else {
                ps.setDate(3, null);
            }
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            ps.setString(6, c.getImage());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(CustomerDTO c) {
        String query = "UPDATE customer SET fullname=?, birthday=?, phone=?, address=?, image=? WHERE customer_id=?";
        try (DBConnectHelper db = new DBConnectHelper();
             Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, c.getFullName());
            // Convert java.util.Date to java.sql.Date
            if (c.getBirthday() != null) {
                ps.setDate(2, new java.sql.Date(c.getBirthday().getTime()));
            } else {
                ps.setDate(2, null);
            }
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setString(5, c.getImage());
            ps.setInt(6, c.getCustomerId());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String query = "UPDATE customer SET status = 0 WHERE customer_id = ?";
        try (DBConnectHelper db = new DBConnectHelper();
             Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
    public boolean isPhoneExists(String phone, int excludeCustomerId) {
        String query = "SELECT COUNT(*) FROM customer WHERE phone = ? AND customer_id != ? AND status = 1";
        try (DBConnectHelper db = new DBConnectHelper();
             Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, phone);
            ps.setInt(2, excludeCustomerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}

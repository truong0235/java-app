package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.bat.DTO.CustomerDTO;
import com.bat.Utils.Helper.DBConnectHelper;

public class CustomerDAL {

    public ArrayList<CustomerDTO> getCustomers() {
        ArrayList<CustomerDTO> customers = new ArrayList<>();
        String query = "SELECT * FROM customer WHERE status = 0";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CustomerDTO customer = new CustomerDTO(
                        rs.getInt("customer_id"),
                        rs.getString("fullname"),
                        rs.getString("birthday"),
                        rs.getString("phone"),
                        rs.getString("address")
                );
                customers.add(customer);
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public int getAutoIncrement() {
        int nextId = 1;
        String query = "SELECT MAX(customer_id) FROM customer";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) nextId = rs.getInt(1) + 1;
            db.closeConnection();
        } catch (Exception e) { e.printStackTrace(); }
        return nextId;
    }

    public boolean add(CustomerDTO c) {
        String query = "INSERT INTO customer (customer_id, fullname, birthday, phone, address) VALUES (?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, c.getCustomer_id());
            ps.setString(2, c.getFullName());
            ps.setString(3, c.getBirthday());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getAddress());
            int result = ps.executeUpdate();
            db.closeConnection();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(CustomerDTO c) {
        String query = "UPDATE customer SET fullname=?, birthday=?, phone=?, address=? WHERE customer_id=?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, c.getFullName());
            ps.setString(2, c.getBirthday());
            ps.setString(3, c.getPhone());
            ps.setString(4, c.getAddress());
            ps.setInt(5, c.getCustomer_id());
            int result = ps.executeUpdate();
            db.closeConnection();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String query = "UPDATE customer SET status = 1 WHERE customer_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            db.closeConnection();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
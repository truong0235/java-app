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
                CustomerDTO customer = new CustomerDTO(
                        rs.getInt("customer_id"),
                        rs.getString("fullname"),
                        rs.getString("birthday"),
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
            ps.setString(3, c.getBirthday());
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
            ps.setString(2, c.getBirthday());
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
}

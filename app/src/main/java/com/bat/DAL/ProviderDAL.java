package com.bat.DAL;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.bat.DTO.ProviderDTO;
import com.bat.GUI.panel.Provider;
import com.bat.Utils.Helper.DBConnectHelper;

public class ProviderDAL {

    public ArrayList<ProviderDTO> getProviders() {
        ArrayList<ProviderDTO> providers = new ArrayList<>();
        String query = "SELECT * FROM provider WHERE status = 1";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProviderDTO provider = new ProviderDTO(
                        rs.getInt("provider_id"),
                        rs.getString("provider_name"),
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                providers.add(provider);
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return providers;
    }

    public int getAutoIncrement() {
        int nextId = 1;
        String query = "SELECT MAX(provider_id) FROM provider";
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

    public boolean add(ProviderDTO p) {
        String query = "INSERT INTO provider (provider_id, provider_name, address, phone, email) VALUES (?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, p.getProviderId());
            ps.setString(2, p.getProviderName());
            ps.setString(3, p.getAddress());
            ps.setString(4, p.getPhone());
            ps.setString(5, p.getEmail());
            int result = ps.executeUpdate();
            db.closeConnection();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(ProviderDTO p) {
        String query = "UPDATE provider SET provider_name=?, address=?, phone=?, email=? WHERE provider_id=?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, p.getProviderName());
            ps.setString(2, p.getAddress());
            ps.setString(3, p.getPhone());
            ps.setString(4, p.getEmail());
            ps.setInt(5, p.getProviderId());
            int result = ps.executeUpdate();
            db.closeConnection();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String query = "UPDATE provider SET status = 0 WHERE provider_id = ?";
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


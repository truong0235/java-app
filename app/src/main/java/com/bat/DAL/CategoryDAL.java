package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.CategoryDTO;
import com.bat.utils.helper.DBConnectHelper;

public class CategoryDAL {

    public ArrayList<CategoryDTO> getCategories() {
        ArrayList<CategoryDTO> list = new ArrayList<>();
        String query = "SELECT * FROM category WHERE status != 0";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()) {
                list.add(new CategoryDTO(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getInt("status")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public int getAutoIncrement() {
        int nextId = 1;
        String query = "SELECT MAX(category_id) FROM category";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ){
            if (rs.next()) nextId = rs.getInt(1) + 1;
            db.closeConnection();
        } catch (Exception e) { e.printStackTrace(); }
        return nextId;
    }

    public boolean add(CategoryDTO c) {
        String query = "INSERT INTO category (category_id, category_name, description, status) VALUES (?, ?, ?, ?)";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, c.getCategoryId());
            ps.setString(2, c.getCategoryName());
            ps.setString(3, c.getDescription());
            ps.setInt(4, c.getStatus());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(CategoryDTO c) {
        String query = "UPDATE category SET category_name=?, description=?, status=? WHERE category_id=?";
        try (
                   DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setString(1, c.getCategoryName());
            ps.setString(2, c.getDescription());
            ps.setInt(3, c.getStatus());
            ps.setInt(4, c.getCategoryId());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        // Xóa mềm
        String query = "UPDATE category SET status = 0 WHERE category_id = ?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
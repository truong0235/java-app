package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.ProductDTO;
import com.bat.utils.helper.DBConnectHelper;

public class ProductDAL {
    public ArrayList<ProductDTO> getProducts() {
        ArrayList<ProductDTO> products = new ArrayList<>();
        try {
            String query = "SELECT * FROM product";
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProductDTO product = new ProductDTO(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("pic"),
                    rs.getInt("category_id"),
                    rs.getString("publisher"),
                    rs.getInt("publish_year"),
                    rs.getString("author"),
                    rs.getString("language"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity"),
                    rs.getInt("status")
                );
                products.add(product);
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public ProductDTO getProductById(int productId){
        ProductDTO product = new ProductDTO();
        try {
            String query = "SELECT * FROM product WHERE product_id = ?";
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new ProductDTO(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("pic"),
                    rs.getInt("category_id"),
                    rs.getString("publisher"),
                    rs.getInt("publish_year"),
                    rs.getString("author"),
                    rs.getString("language"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity"),
                    rs.getInt("status")
                );
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public ProductDTO getProductByLotId(int lotId){
        ProductDTO product = new ProductDTO();
        try {
            String query = "SELECT p.* FROM product p JOIN lot l ON p.product_id = l.product_id WHERE l.lot_id = ?";
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, lotId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new ProductDTO(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("pic"),
                    rs.getInt("category_id"),
                    rs.getString("publisher"),
                    rs.getInt("publish_year"),
                    rs.getString("author"),
                    rs.getString("language"),
                    rs.getBigDecimal("price"),
                    rs.getInt("quantity"),
                    rs.getInt("status")
                );
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public boolean update(ProductDTO product) {
        String query = "UPDATE product SET product_name = ?, pic = ?, category_id = ?, publisher = ?, publish_year = ?, author = ?, language = ?, price = ?, quantity = ?, status = ? WHERE product_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getPic());
            ps.setInt(3, product.getCategoryId());
            ps.setString(4, product.getPublisher());
            ps.setInt(5, product.getPublishYear());
            ps.setString(6, product.getAuthor());
            ps.setString(7, product.getLanguage());
            ps.setBigDecimal(8, product.getPrice());
            ps.setInt(9, product.getQuantity());
            ps.setInt(10, product.getStatus());
            ps.setInt(11, product.getProductId());
            int affectedRows = ps.executeUpdate();
            db.closeConnection();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateQuantityByLotId(int lotId, int qty) {
        String query = "UPDATE product SET quantity = quantity + ? WHERE product_id = (SELECT product_id FROM lot WHERE lot_id = ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, qty);
            ps.setInt(2, lotId);
            int affectedRows = ps.executeUpdate();
            db.closeConnection();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
}

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
        String query = "SELECT * FROM product WHERE status != 0";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    public ProductDTO getProductById(int productId){
        ProductDTO product = new ProductDTO();
        String query = "SELECT * FROM product WHERE product_id = ? AND status != 0";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new ProductDTO(
                        rs.getInt("product_id"), rs.getString("product_name"), rs.getString("pic"),
                        rs.getInt("category_id"), rs.getString("publisher"), rs.getInt("publish_year"),
                        rs.getString("author"), rs.getString("language"), rs.getBigDecimal("price"),
                        rs.getInt("quantity"), rs.getInt("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    // --- CÁC HÀM CŨ ĐƯỢC GIỮ LẠI ĐỂ KHÔNG BỊ LỖI MODULE KHÁC ---
    public ProductDTO getProductByLotId(int lotId){
        ProductDTO product = null;
        String query = "SELECT p.* FROM product p JOIN lot l ON p.product_id = l.product_id WHERE l.lot_id = ? AND p.status != 0";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, lotId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = new ProductDTO(
                        rs.getInt("product_id"), rs.getString("product_name"), rs.getString("pic"),
                        rs.getInt("category_id"), rs.getString("publisher"), rs.getInt("publish_year"),
                        rs.getString("author"), rs.getString("language"), rs.getBigDecimal("price"),
                        rs.getInt("quantity"), rs.getInt("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public ArrayList<ProductDTO> getPrInImport(int inmportId) {
        ArrayList<ProductDTO> prInImport = new ArrayList<>();
        String query = "SELECT DISTINCT p.product_id, p.product_name, sum(l.initial_quantity) as qty, sum(l.initial_quantity * l.import_price) as price " +
                        "FROM Product p " +
                        "JOIN Lot l ON p.product_id = l.product_id " +
                        "WHERE l.import_id = ? AND l.status != 'Xóa' AND p.status != 0 " +
                        "GROUP BY p.product_id, p.product_name";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            
            ps.setInt(1, inmportId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ProductDTO prd = new ProductDTO();
                prd.setProductId(rs.getInt("product_id"));
                prd.setProductName(rs.getString("product_name"));
                prd.setQuantity(rs.getInt("qty"));
                prd.setPrice(rs.getBigDecimal("price"));
                prInImport.add(prd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prInImport;
    }

    public boolean update(ProductDTO product) {
        String query = "UPDATE product SET product_name = ?, pic = ?, category_id = ?, publisher = ?, publish_year = ?, author = ?, language = ?, price = ?, quantity = ?, status = ? WHERE product_id = ?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
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
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateQuantityByLotId(int lotId, int qty) {
        String query = "UPDATE product SET quantity = quantity + ? WHERE product_id = (SELECT product_id FROM lot WHERE lot_id = ?)";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, qty);
            ps.setInt(2, lotId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // --- CÁC HÀM MỚI CHO GIAO DIỆN QUẢN LÝ ---
    public int getAutoIncrement() {
        int nextId = 1;
        String query = "SELECT MAX(product_id) FROM product";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ){
            if (rs.next()) nextId = rs.getInt(1) + 1;
        } catch (Exception e) { e.printStackTrace(); }
        return nextId;
    }

    public boolean add(ProductDTO p) {
        String query = "INSERT INTO product (product_id, product_name, pic, category_id, publisher, publish_year, author, language, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ){
            ps.setInt(1, p.getProductId());
            ps.setString(2, p.getProductName());
            ps.setString(3, p.getPic());
            ps.setInt(4, p.getCategoryId());
            ps.setString(5, p.getPublisher());
            ps.setInt(6, p.getPublishYear());
            ps.setString(7, p.getAuthor());
            ps.setString(8, p.getLanguage());
            ps.setBigDecimal(9, p.getPrice());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // public boolean update(ProductDTO p) {
    //     String query = "UPDATE product SET product_name=?, pic=?, category_id=?, publisher=?, publish_year=?, author=?, language=?, price=?, quantity=?, status=? WHERE product_id=?";
    //     try {
    //         DBConnectHelper db = new DBConnectHelper();
    //         Connection conn = db.getConnection();
    //         PreparedStatement ps = conn.prepareStatement(query);
    //         ps.setString(1, p.getProductName());
    //         ps.setString(2, p.getPic());
    //         ps.setInt(3, p.getCategoryId());
    //         ps.setString(4, p.getPublisher());
    //         ps.setInt(5, p.getPublishYear());
    //         ps.setString(6, p.getAuthor());
    //         ps.setString(7, p.getLanguage());
    //         ps.setBigDecimal(8, p.getPrice());
    //         ps.setInt(9, p.getQuantity());
    //         ps.setInt(10, p.getStatus());
    //         ps.setInt(11, p.getProductId());
    //         int result = ps.executeUpdate();
    //         db.closeConnection();
    //         return result > 0;
    //     } catch (Exception e) { e.printStackTrace(); }
    //     return false;
    // }

    public boolean delete(int id) {
        String query = "UPDATE product SET status = 0 WHERE product_id = ?";
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
}
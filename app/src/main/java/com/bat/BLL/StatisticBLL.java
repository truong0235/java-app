package com.bat.BLL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bat.utils.helper.DBConnectHelper;

public class StatisticBLL {
    // Lấy thống kê theo danh mục (cho biểu đồ tròn) - Top 5 danh mục
    public Map<String, Double> getProductStatistics() {
        Map<String, Double> categoryStats = new HashMap<>();
        String query = "SELECT p.product_name, SUM(el.quantity) as total_sold "
                        + "FROM product p "
                        + "INNER JOIN export_lot el ON p.product_id = el.product_id "
                        + "WHERE p.status != 0 "
                        + "GROUP BY p.product_id, p.product_name "
                        + "ORDER BY total_sold DESC "  
                        + "LIMIT 5";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                categoryStats.put(rs.getString("product_name"), rs.getDouble("total_sold"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return categoryStats;
    }
    
    // Top khách hàng (theo tổng tiền xuất)
    public List<Object[]> getTopCustomers(int limit) {
        List<Object[]> topCustomers = new ArrayList<>();
        String query = "SELECT c.fullname, c.phone, COUNT(DISTINCT e.export_id) as order_count, " +
                      "COALESCE(SUM(e.total_price), 0) as total_amount " +
                      "FROM customer c " +
                      "LEFT JOIN export_receipt e ON c.customer_id = e.customer_id AND e.status != 0 " +
                      "WHERE c.status != 0 " +
                      "GROUP BY c.customer_id, c.fullname, c.phone " +
                      "ORDER BY total_amount DESC " +
                      "LIMIT ?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("fullname");
                row[1] = rs.getString("phone");
                row[2] = rs.getInt("order_count");
                row[3] = rs.getDouble("total_amount");
                topCustomers.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topCustomers;
    }
    
    // Top nhà cung cấp (theo tổng tiền nhập)
    public List<Object[]> getTopProviders(int limit) {
        List<Object[]> topProviders = new ArrayList<>();
        String query = "SELECT p.provider_name, p.phone, COUNT(DISTINCT i.import_id) as order_count, " +
                      "COALESCE(SUM(i.total_price), 0) as total_amount " +
                      "FROM provider p " +
                      "LEFT JOIN import_receipt i ON p.provider_id = i.provider_id AND i.status != 0 " +
                      "WHERE p.status != 0 " +
                      "GROUP BY p.provider_id, p.provider_name, p.phone " +
                      "ORDER BY total_amount DESC " +
                      "LIMIT ?";
        try (
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
        ) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("provider_name");
                row[1] = rs.getString("phone");
                row[2] = rs.getInt("order_count");
                row[3] = rs.getDouble("total_amount");
                topProviders.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return topProviders;
    }
}

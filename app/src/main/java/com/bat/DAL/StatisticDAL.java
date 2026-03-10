package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bat.DTO.Statistic.ThongKeDoanhThuDTO;
import com.bat.DTO.Statistic.ThongKeTheoThangDTO;
import com.bat.DTO.Statistic.ThongKeTungNgayTrongThangDTO;
import com.bat.utils.helper.DBConnectHelper;

public class StatisticDAL {
    public Map<String, Double> getProductStatistics() {
        Map<String, Double> categoryStats = new HashMap<>();
        String query = "SELECT p.product_name, SUM(el.quantity) as total_sold "
                        + "FROM product p "
                        + "INNER JOIN export_lot el ON p.product_id = el.product_id "
                        + "INNER JOIN export_receipt e ON el.export_id = e.export_id AND e.status != 0 "
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

    public ArrayList<ThongKeDoanhThuDTO> getDoanhThuTheoTungNam(int year_start, int year_end) {
        ArrayList<ThongKeDoanhThuDTO> result = new ArrayList<>();
        try (DBConnectHelper db = new DBConnectHelper();
             Connection con = db.getConnection()) {
            
            String sqlSetStartYear = "SET @start_year = ?;";
            String sqlSetEndYear = "SET @end_year = ?;";
            String sqlSelect = """
                     WITH RECURSIVE years(year) AS (
                       SELECT @start_year
                       UNION ALL
                       SELECT year + 1
                       FROM years
                       WHERE year < @end_year
                     )
                     SELECT 
                       years.year AS nam,
                       COALESCE(SUM(l.import_price * el.quantity), 0) AS chiphi, 
                       COALESCE(SUM(el.export_price * el.quantity), 0) AS doanhthu
                     FROM years
                     LEFT JOIN export_receipt er ON YEAR(er.export_date) = years.year AND er.status = 1
                     LEFT JOIN export_lot el ON er.export_id = el.export_id
                     LEFT JOIN lot l ON el.lot_id = l.lot_id
                     GROUP BY years.year
                     ORDER BY years.year;""";
            
            PreparedStatement pstStartYear = con.prepareStatement(sqlSetStartYear);
            PreparedStatement pstEndYear = con.prepareStatement(sqlSetEndYear);
            PreparedStatement pstSelect = con.prepareStatement(sqlSelect);

            pstStartYear.setInt(1, year_start);
            pstEndYear.setInt(1, year_end);

            pstStartYear.execute();
            pstEndYear.execute();

            ResultSet rs = pstSelect.executeQuery();
            while (rs.next()) {
                int thoigian = rs.getInt("nam");
                Long chiphi = rs.getLong("chiphi");
                Long doanhthu = rs.getLong("doanhthu");
                ThongKeDoanhThuDTO x = new ThongKeDoanhThuDTO(thoigian, chiphi, doanhthu, doanhthu - chiphi);
                result.add(x);
            }
        } catch (Exception e) {
            System.err.println("Error in getDoanhThuTheoTungNam: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<ThongKeTheoThangDTO> getDoanhThuTheoTungThang(int year) {
        ArrayList<ThongKeTheoThangDTO> result = new ArrayList<>();
        try (DBConnectHelper db = new DBConnectHelper();
            Connection con = db.getConnection()) {
            
            String sqlSetYear = "SET @year = ?;";
            String sqlSetStartMonth = "SET @start_month = 1;";
            String sqlSetEndMonth = "SET @end_month = 12;";
            String sqlSelect = """
                    WITH RECURSIVE months(month) AS (
                    SELECT @start_month
                    UNION ALL
                    SELECT month + 1
                    FROM months
                    WHERE month < @end_month
                    )
                    SELECT 
                    months.month AS thang,
                    COALESCE(SUM(l.import_price * el.quantity), 0) AS chiphi, 
                    COALESCE(SUM(el.export_price * el.quantity), 0) AS doanhthu
                    FROM months
                    LEFT JOIN export_receipt er ON YEAR(er.export_date) = @year 
                    AND MONTH(er.export_date) = months.month 
                    AND er.status = 1
                    LEFT JOIN export_lot el ON er.export_id = el.export_id
                    LEFT JOIN lot l ON el.lot_id = l.lot_id
                    GROUP BY months.month
                    ORDER BY months.month;""";
            
            PreparedStatement pstYear = con.prepareStatement(sqlSetYear);
            PreparedStatement pstSelect = con.prepareStatement(sqlSelect);
            PreparedStatement pstStartMonth = con.prepareStatement(sqlSetStartMonth);
            PreparedStatement pstEndMonth = con.prepareStatement(sqlSetEndMonth);

            pstYear.setInt(1, year);
            // pstStartMonth.setInt(1, month_start);
            // pstEndMonth.setInt(1, month_end);

            pstYear.execute();
            pstStartMonth.execute();
            pstEndMonth.execute();

            ResultSet rs = pstSelect.executeQuery();
            while (rs.next()) {
                int thang = rs.getInt("thang");
                Long chiphi = rs.getLong("chiphi");
                Long doanhthu = rs.getLong("doanhthu");
                ThongKeTheoThangDTO x = new ThongKeTheoThangDTO(thang, chiphi, doanhthu, doanhthu - chiphi);
                result.add(x);
            }
        } catch (Exception e) {
            System.err.println("Error in getDoanhThuTheoTungThang: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<ThongKeTungNgayTrongThangDTO> getThongKeTuNgayDenNgay(String start, String end) {
        ArrayList<ThongKeTungNgayTrongThangDTO> result = new ArrayList<>();
        try (DBConnectHelper db = new DBConnectHelper();
            Connection con = db.getConnection()) {
            
            String setStart = "SET @start_date = ?;";
            String setEnd = "SET @end_date = ?;";
            String sqlSelect = """
                    SELECT 
                    dates.date AS ngay, 
                    COALESCE(SUM(l.import_price * el.quantity), 0) AS chiphi, 
                    COALESCE(SUM(el.export_price * el.quantity), 0) AS doanhthu
                    FROM (
                    SELECT DATE_ADD(@start_date, INTERVAL c.number DAY) AS date
                    FROM (
                        SELECT a.number + b.number * 31 AS number
                        FROM (
                        SELECT 0 AS number
                        UNION ALL SELECT 1
                        UNION ALL SELECT 2
                        UNION ALL SELECT 3
                        UNION ALL SELECT 4
                        UNION ALL SELECT 5
                        UNION ALL SELECT 6
                        UNION ALL SELECT 7
                        UNION ALL SELECT 8
                        UNION ALL SELECT 9
                        UNION ALL SELECT 10
                        UNION ALL SELECT 11
                        UNION ALL SELECT 12
                        UNION ALL SELECT 13
                        UNION ALL SELECT 14
                        UNION ALL SELECT 15
                        UNION ALL SELECT 16
                        UNION ALL SELECT 17
                        UNION ALL SELECT 18
                        UNION ALL SELECT 19
                        UNION ALL SELECT 20
                        UNION ALL SELECT 21
                        UNION ALL SELECT 22
                        UNION ALL SELECT 23
                        UNION ALL SELECT 24
                        UNION ALL SELECT 25
                        UNION ALL SELECT 26
                        UNION ALL SELECT 27
                        UNION ALL SELECT 28
                        UNION ALL SELECT 29
                        UNION ALL SELECT 30
                        ) AS a
                        CROSS JOIN (
                        SELECT 0 AS number
                        UNION ALL SELECT 1
                        UNION ALL SELECT 2
                        UNION ALL SELECT 3
                        UNION ALL SELECT 4
                        UNION ALL SELECT 5
                        UNION ALL SELECT 6
                        UNION ALL SELECT 7
                        UNION ALL SELECT 8
                        UNION ALL SELECT 9
                        UNION ALL SELECT 10
                        ) AS b
                    ) AS c
                    WHERE DATE_ADD(@start_date, INTERVAL c.number DAY) <= @end_date
                    ) AS dates
                    LEFT JOIN export_receipt er ON DATE(er.export_date) = dates.date AND er.status = 1
                    LEFT JOIN export_lot el ON er.export_id = el.export_id
                    LEFT JOIN lot l ON el.lot_id = l.lot_id
                    GROUP BY dates.date
                    ORDER BY dates.date;""";

            PreparedStatement pstStart = con.prepareStatement(setStart);
            PreparedStatement pstEnd = con.prepareStatement(setEnd);
            PreparedStatement pstSelect = con.prepareStatement(sqlSelect);

            pstStart.setString(1, start);
            pstEnd.setString(1, end);

            pstStart.execute();
            pstEnd.execute();
            
            ResultSet rs = pstSelect.executeQuery();
            while (rs.next()) {
                Date ngay = rs.getDate("ngay");
                Long chiphi = rs.getLong("chiphi");
                Long doanhthu = rs.getLong("doanhthu");
                Long loinhuan = doanhthu - chiphi;
                ThongKeTungNgayTrongThangDTO tn = new ThongKeTungNgayTrongThangDTO(ngay, chiphi, doanhthu, loinhuan);
                result.add(tn);
            }
        } catch (Exception e) {
            System.err.println("Error in getThongKeTuNgayDenNgay: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}

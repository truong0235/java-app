package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.bat.DTO.LotDTO;
import com.bat.DTO.LotTransactionDTO;
import com.bat.DTO.ProductDTO;
import com.bat.utils.helper.DBConnectHelper;

public class LotDAL {
    public ArrayList<LotDTO> getLots() {
        ArrayList<LotDTO> lotList = new ArrayList<>();
        String query = "SELECT lot_id, lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id " + 
                        "FROM Lot";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = (ResultSet) ps.executeQuery();
            while(rs.next()) {
                LotDTO lot = new LotDTO(
                    rs.getInt("lot_id"),
                    rs.getString("lot_code"),
                    rs.getInt("product_id"),
                    rs.getInt("import_id"),
                    rs.getInt("initial_quantity"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("import_price"),
                    rs.getInt("print_year"),
                    rs.getString("status"),
                    rs.getTimestamp("import_date").toLocalDateTime()
                );
                lotList.add(lot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lotList;
    }

    public ArrayList<LotDTO> getLotsByImpId(int impId) {
        ArrayList<LotDTO> lotList = new ArrayList<>();
        String query = "SELECT lot_id, lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id " + 
                        "FROM Lot WHERE import_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, impId);
            ResultSet rs = (ResultSet) ps.executeQuery();
            while(rs.next()) {
                LotDTO lot = new LotDTO(
                    rs.getInt("lot_id"),
                    rs.getString("lot_code"),
                    rs.getInt("product_id"),
                    rs.getInt("import_id"),
                    rs.getInt("initial_quantity"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("import_price"),
                    rs.getInt("print_year"),
                    rs.getString("status"),
                    rs.getTimestamp("import_date").toLocalDateTime()
                );
                lotList.add(lot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lotList;
    }

    public ArrayList<LotDTO> getLotsByProductId(int productId) {
        ArrayList<LotDTO> lotList = new ArrayList<>();
        String query = "SELECT lot_id, lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id " + 
                        "FROM Lot WHERE product_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, productId);
            ResultSet rs = (ResultSet) ps.executeQuery();
            while(rs.next()) {
                LotDTO lot = new LotDTO(
                    rs.getInt("lot_id"),
                    rs.getString("lot_code"),
                    rs.getInt("product_id"),
                    rs.getInt("import_id"),
                    rs.getInt("initial_quantity"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("import_price"),
                    rs.getInt("print_year"),
                    rs.getString("status"),
                    rs.getTimestamp("import_date").toLocalDateTime()
                );
                lotList.add(lot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lotList;
    }

    public LotDTO getLotById(int lotId) {
        String query = "SELECT lot_id, lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id " + 
                        "FROM Lot WHERE lot_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, lotId);
            ResultSet rs = (ResultSet) ps.executeQuery();
            if(rs.next()) {
                LotDTO lot = new LotDTO(
                    rs.getInt("lot_id"),
                    rs.getString("lot_code"),
                    rs.getInt("product_id"),
                    rs.getInt("import_id"),
                    rs.getInt("initial_quantity"),
                    rs.getInt("quantity"),
                    rs.getBigDecimal("import_price"),
                    rs.getInt("print_year"),
                    rs.getString("status"),
                    rs.getTimestamp("import_date").toLocalDateTime()
                );
                return lot;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int add(LotDTO lot) { // thêm lot, update SL sp và ghi nhận lịch sử nhập kho
        String query = "INSERT INTO Lot (lot_code, product_id, import_id, initial_quantity, quantity, import_price, print_year, status, import_date) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, lot.getLotCode());
            ps.setInt(2, lot.getProductId());
            ps.setInt(3, lot.getImportId());
            ps.setInt(4, lot.getInitialQuantity());
            ps.setInt(5, lot.getQuantity());
            ps.setBigDecimal(6, lot.getImportPrice());
            ps.setInt(7, lot.getPrintYear());
            ps.setString(8, lot.getStatus());
            ps.setTimestamp(9, Timestamp.valueOf(lot.getImportDate()));
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int lotId = rs.getInt(1);
                        LotTransactionDTO trans = new LotTransactionDTO(
                            0,
                            lotId,
                            lot.getImportId(),
                            lot.getInitialQuantity(),
                            lot.getInitialQuantity(),
                            lot.getImportDate(),
                            "import"
                        );
                        LotTransactionDAL transDAL = new LotTransactionDAL();
                        ProductDAL productDAL = new ProductDAL();

                        transDAL.add(trans);
                        productDAL.updateQuantityByLotId(lotId, lot.getInitialQuantity());
                        return lotId;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean delete(int lotId) {
        String query = "UPDATE lot SET status = 'Xoa' WHERE lot_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, lotId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByImpId(int impId) {
        String query = "UPDATE lot SET status = 'Xoa' WHERE import_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, impId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ArrayList<LotDTO> l, int impId) {
        boolean result = this.deleteByImpId(impId);
        if (result) {
            for (LotDTO lot : l) {
                int res = this.add(lot);
                if (res == -1) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean updateQuantity(int lotId, int quantity) {
        String query = "UPDATE lot SET quantity = quantity + ? WHERE lot_id = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, quantity);
            ps.setInt(2, lotId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLotCodeExists(String lotCode) {
        String query = "SELECT COUNT(*) AS count FROM Lot WHERE lot_code = ?";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, lotCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<ProductDTO> getPrInImport(int inmportId) {
        ArrayList<ProductDTO> prInImport = new ArrayList<>();
        String query = "SELECT DISTINCT p.product_id, p.product_name, sum(l.initial_quantity) as qty, sum(l.initial_quantity * l.import_price) as price " +
                        "FROM Product p " +
                        "JOIN Lot l ON p.product_id = l.product_id " +
                        "WHERE l.import_id = ? " +
                        "GROUP BY p.product_id, p.product_name";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
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
}

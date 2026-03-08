package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.utils.helper.DBConnectHelper;

public class ExportHandleDAL {
    public static class ExportHandle {
        private int exportId;
        private String exportDate;
        private int status;
        private double totalPrice;
        private int userId;
        private String workerName;
        private String customerName;

        public ExportHandle(int exportId, String exportDate, int status, double totalPrice, int userId, String workerName, String customerName) {
            this.exportId = exportId;
            this.exportDate = exportDate;
            this.status = status;
            this.totalPrice = totalPrice;
            this.userId = userId;
            this.workerName = workerName;
            this.customerName = customerName;
        }

        public int getExportId() {
            return exportId;
        }
        
        public String getExportDate() {
            return exportDate;
        }

        public int getStatus() {
            return status;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public int getUserId() {
            return userId;
        }

        public String getWorkerName() {
            return workerName;
        }

        public String getCustomerName() {
            return customerName;
        }

    }

    public static ArrayList<ExportHandle> exportTableQuery() {
        String query = "SELECT export_receipt.export_id, export_receipt.export_date, export_receipt.status, export_receipt.total_price, export_receipt.user_id, users.fullname as workername, customer.fullname as customer_name FROM export_receipt JOIN users ON users.user_id = export_receipt.user_id JOIN customer ON customer.customer_id = export_receipt.customer_id;";
        ArrayList<ExportHandle> temp = new ArrayList<>();
        try(
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
        ){
            while (rs.next()) {
                ExportHandle unit = new ExportHandleDAL.ExportHandle(
                    rs.getInt("export_id"),
                    rs.getString("export_date"),
                    rs.getInt("status"),
                    rs.getDouble("total_price"),
                    rs.getInt("user_id"),
                    rs.getString("workername"),
                    rs.getString("customer_name")
                );
                temp.add(unit);
            }
        } catch (Exception e) {
            e.printStackTrace();
                
            }
       return temp;
    }
}

package com.bat.BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bat.DAL.StatisticDAL;
import com.bat.DTO.Statistic.ThongKeDoanhThuDTO;
import com.bat.DTO.Statistic.ThongKeTheoThangDTO;
import com.bat.DTO.Statistic.ThongKeTungNgayTrongThangDTO;

public class StatisticBLL {
    // Lấy thống kê theo danh mục (cho biểu đồ tròn) - Top 5 danh mục
    private final StatisticDAL statisticDAL = new StatisticDAL();
    public Map<String, Double> getProductStatistics() {
        return statisticDAL.getProductStatistics();
    }
    
    // Top khách hàng (theo tổng tiền xuất)
    public List<Object[]> getTopCustomers(int limit) {
        return statisticDAL.getTopCustomers(limit);
    }

    // Top nhà cung cấp (theo tổng tiền nhập)
    public List<Object[]> getTopProviders(int limit) {
        return statisticDAL.getTopProviders(limit);
    }

    public ArrayList<ThongKeDoanhThuDTO> getDoanhThuTheoTungNam(int year_start, int year_end) {
        return this.statisticDAL.getDoanhThuTheoTungNam(year_start, year_end);
    }
    
    public ArrayList<ThongKeTheoThangDTO> getDoanhThuTheoTungThang(int nam){
        return statisticDAL.getDoanhThuTheoTungThang(nam);
    }
    
    public ArrayList<ThongKeTungNgayTrongThangDTO> getThongKeTuNgayDenNgay(String start, String end){
        return statisticDAL.getThongKeTuNgayDenNgay(start, end);
    }
        


}

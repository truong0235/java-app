package com.bat.BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bat.DAL.StatisticDAL;
import com.bat.DTO.Statistic.ThongKeDoanhThuDTO;
import com.bat.DTO.Statistic.ThongKeTheoThangDTO;
import com.bat.DTO.Statistic.ThongKeTungNgayTrongThangDTO;

public class StatisticBLL {
    private final StatisticDAL statisticDAL = new StatisticDAL();
    public Map<String, Double> getProductStatistics() {
        return statisticDAL.getProductStatistics();
    }
    
    public List<Object[]> getTopCustomers(int limit) {
        return statisticDAL.getTopCustomers(limit);
    }

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

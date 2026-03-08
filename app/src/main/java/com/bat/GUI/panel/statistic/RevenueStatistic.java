package com.bat.GUI.panel.statistic;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.bat.BLL.StatisticBLL;

/**
 *
 * @author Tran Nhat Sinh
 */
public class RevenueStatistic extends JPanel {

    JTabbedPane tabbedPane;
    RevenueStatisticByYear thongketungnam;
    RevenueStatisticByMonth thongkedoanhthutungthang;
    RevenueStatisticByDateRange thongkedoanhthutungaydenngay;
    Color BackgroundColor = new Color(240, 247, 250);
    StatisticBLL statisticBLL;

    public RevenueStatistic(StatisticBLL statisticBLL) {
        this.statisticBLL = statisticBLL;
        initComponent();
    }

    public void initComponent() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(BackgroundColor);

        thongketungnam = new RevenueStatisticByYear(statisticBLL);
        thongkedoanhthutungthang = new RevenueStatisticByMonth(statisticBLL);
        thongkedoanhthutungaydenngay = new RevenueStatisticByDateRange(statisticBLL);

        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab("Thống kê theo năm", thongketungnam);
        tabbedPane.addTab("Thống kê từng tháng trong năm", thongkedoanhthutungthang);
        tabbedPane.addTab("Thống kê từ ngày đến ngày", thongkedoanhthutungaydenngay);

        this.add(tabbedPane);
    }
}

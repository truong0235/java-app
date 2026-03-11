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
public final class Statistic extends JPanel {

    JTabbedPane tabbedPane;
    JPanel dashboard;
    JPanel revenueStatistic;
    Color BackgroundColor = new Color(240, 247, 250);

    public Statistic() {
        initComponent();
    }

    public void initComponent() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(BackgroundColor);

        dashboard = new Dashboard();
        revenueStatistic = new RevenueStatistic(new StatisticBLL());

        tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.addTab("Tổng quan", dashboard);
        tabbedPane.addTab("Doanh thu", revenueStatistic);

        this.add(tabbedPane);
    }
}

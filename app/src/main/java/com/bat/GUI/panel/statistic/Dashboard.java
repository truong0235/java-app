package com.bat.GUI.panel.statistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.bat.BLL.CustomerBLL;
import com.bat.BLL.ProductBLL;
import com.bat.BLL.StatisticBLL;
import com.bat.BLL.UserBLL;
import com.bat.GUI.component.ItemTaskbar;
import com.bat.GUI.component.TopCustomerPanel;
import com.bat.GUI.component.TopProviderPanel;
import com.bat.GUI.component.chart.piechart.ModelPieChart;
import com.bat.GUI.component.chart.piechart.PieChart;

public class Dashboard extends JPanel {
    
    private final StatisticBLL statisticBLL;
    private PieChart categoryPieChart;
    private TopCustomerPanel topCustomerPanel;
    private TopProviderPanel topProviderPanel;
    private JPanel legendPanel;
    ItemTaskbar[] listitem;

    private ProductBLL productBLL = new ProductBLL();
    private CustomerBLL customerBLL = new CustomerBLL();
    private UserBLL userBLL = new UserBLL();

    
    String[][] getSt = {
        {"Sản phẩm hiện có trong kho", "productt.svg", Integer.toString(productBLL.getProductsList().size())},
        {"Khách từ trước đến nay", "stafff.svg", Integer.toString(customerBLL.getCustomerList().size())},
        {"Nhân viên đang hoạt động", "customerr.svg", Integer.toString(userBLL.getUserList().size())}
    };

    
    public Dashboard() {
        this.statisticBLL = new StatisticBLL();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(228, 238, 255));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 3, 20, 0));
        topPanel.setOpaque(false);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setPreferredSize(new Dimension(0, 120));
 
        listitem = new ItemTaskbar[getSt.length];
        for (int i = 0; i < getSt.length; i++) {
            listitem[i] = new ItemTaskbar(getSt[i][1], getSt[i][2], getSt[i][0], 0);
            topPanel.add(listitem[i]);
        }
        add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        
        JPanel leftPanel = createPanelWithTitle("Top 5 sản phẩm bán chạy");
        leftPanel.setPreferredSize(new Dimension(400, 400));
        
        categoryPieChart = new PieChart();
        categoryPieChart.setPreferredSize(new Dimension(350, 200));
        leftPanel.add(categoryPieChart, BorderLayout.CENTER);
        
        legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        legendPanel.setPreferredSize(new Dimension(350, 80));
        legendPanel.setOpaque(false);
        leftPanel.add(legendPanel, BorderLayout.SOUTH);
        
        centerPanel.add(leftPanel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        rightPanel.setOpaque(false);
        
        topCustomerPanel = new TopCustomerPanel();
        topProviderPanel = new TopProviderPanel();
        
        rightPanel.add(topCustomerPanel);
        rightPanel.add(topProviderPanel);
        
        centerPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createPanelWithTitle(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }
    
    public void loadData() {
        categoryPieChart.clear();
        Map<String, Double> categoryStats = statisticBLL.getProductStatistics();
        
        Color[] colors = {
            new Color(52, 152, 219), 
            new Color(46, 204, 113), 
            new Color(241, 196, 15), 
            new Color(231, 76, 60),  
            new Color(155, 89, 182)  
        };
        
        legendPanel.removeAll();
        
        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : categoryStats.entrySet()) {
            Color color = colors[colorIndex % colors.length];
            categoryPieChart.addItem(new ModelPieChart(
                entry.getKey(),
                entry.getValue(),
                color
            ));
            
            addLegendItem(legendPanel, entry.getKey(), color);
            colorIndex++;
        }
        categoryPieChart.start();
        
        legendPanel.revalidate();
        legendPanel.repaint();
        
        List<Object[]> topCustomers = statisticBLL.getTopCustomers(5);
        topCustomerPanel.setData(topCustomers);
        
        List<Object[]> topProviders = statisticBLL.getTopProviders(5);
        topProviderPanel.setData(topProviders);    }
    
    public void refresh() {
        loadData();
    }
    
    private void addLegendItem(JPanel legendPanel, String categoryName, Color color) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        itemPanel.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        JLabel nameLabel = new JLabel(categoryName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(new Color(80, 80, 80));
        
        itemPanel.add(colorBox);
        itemPanel.add(nameLabel);
        legendPanel.add(itemPanel);
    }
}


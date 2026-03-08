package com.bat.GUI.panel.statistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.bat.BLL.StatisticBLL;
import com.bat.DTO.Statistic.ThongKeDoanhThuDTO;
import com.bat.GUI.component.chart.barchart.Chart;
import com.bat.GUI.component.chart.barchart.ModelChart;
import com.bat.utils.helper.ExcelExporter;

public class RevenueStatisticByYear extends JPanel implements ActionListener {
    
    private final StatisticBLL statisticBLL;
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    
    // UI Components
    private JTextField txtFromYear;
    private JTextField txtToYear;
    private JButton btnStatistic;
    private JButton btnRefresh;
    private JButton btnExportExcel;
    
    private Chart chart;
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    
    private ArrayList<ThongKeDoanhThuDTO> currentData;
    
    public RevenueStatisticByYear(StatisticBLL statisticBLL) {
        this.statisticBLL = statisticBLL;
        initComponents();
        loadDefaultData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(228, 238, 255));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Top panel - Filter controls
        add(createFilterPanel(), BorderLayout.NORTH);
        
        // Center panel - Chart
        chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);
        
        // Bottom panel - Table
        add(createTablePanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createFilterPanel() { 
        // return panel;
        JPanel pnl_top = new JPanel(new FlowLayout());
        JLabel lblChonNamBatDau, lblChonNamKetThuc;
        lblChonNamBatDau = new JLabel("Từ năm");
        lblChonNamKetThuc = new JLabel("Đến năm");

        txtFromYear = new JTextField("");
        txtToYear = new JTextField("");

        btnStatistic = new JButton("Thống kê");
        btnRefresh = new JButton("Làm mới");
        btnExportExcel = new JButton("Xuất excel");
        btnStatistic.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnExportExcel.addActionListener(this);

        pnl_top.add(lblChonNamBatDau);
        pnl_top.add(txtFromYear);
        pnl_top.add(lblChonNamKetThuc);
        pnl_top.add(txtToYear);
        pnl_top.add(btnStatistic);
        pnl_top.add(btnRefresh);
        pnl_top.add(btnExportExcel);

        return pnl_top;
    }
    
    // private JButton createButton(String text, Color bgColor) {
    //     JButton button = new JButton(text);
    //     button.setPreferredSize(new Dimension(120, 35));
    //     button.setFont(new Font("Segoe UI", Font.BOLD, 13));
    //     button.setBackground(bgColor);
    //     button.setForeground(Color.WHITE);
    //     button.setFocusPainted(false);
    //     button.setBorderPainted(false);
    //     button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    //     return button;
    // }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(0, 400));
        
        chart = new Chart();
        chart.addLegend("Vốn", new Color(255, 193, 7));
        chart.addLegend("Doanh thu", new Color(13, 202, 240));
        chart.addLegend("Lợi nhuận", new Color(168, 85, 247));
        panel.add(chart, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(0, 300));
        
        String[] columns = {"Năm", "Vốn", "Doanh thu", "Lợi nhuận"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadDefaultData() {
        int currentYear = Year.now().getValue();
        int fromYear = currentYear - 5;
        loadData(fromYear, currentYear);
    }
    
    private void loadData(int fromYear, int toYear) {
        try {
            // Get data from BLL
            currentData = statisticBLL.getDoanhThuTheoTungNam(fromYear, toYear);
            
            // Clear table
            tableModel.setRowCount(0);
            
            // Update chart and table
            for (ThongKeDoanhThuDTO dto : currentData) {
                // Add to chart
                String label = "Năm " + dto.getThoigian();
                double[] values = {
                    dto.getVon().doubleValue(),
                    dto.getDoanhthu().doubleValue(),
                    dto.getLoinhuan().doubleValue()
                };
                chart.addData(new ModelChart(label, values));
                
                // Add to table
                Object[] row = {
                    dto.getThoigian(),
                    CURRENCY_FORMATTER.format(dto.getVon()),
                    CURRENCY_FORMATTER.format(dto.getDoanhthu()),
                    CURRENCY_FORMATTER.format(dto.getLoinhuan())
                };
                tableModel.addRow(row);
            }
            
            // Refresh chart
            chart.repaint();
            chart.revalidate();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnStatistic) {
            handleStatistic();
        } else if (e.getSource() == btnRefresh) {
            handleRefresh();
        } else if (e.getSource() == btnExportExcel) {
            handleExportExcel();
        }
    }
    
    private void handleStatistic() {
        try {
            // Check empty fields
            if (txtFromYear.getText().trim().isEmpty() || txtToYear.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ năm bắt đầu và năm kết thúc!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int fromYear = Integer.parseInt(txtFromYear.getText().trim());
            int toYear = Integer.parseInt(txtToYear.getText().trim());
            
            if (fromYear > Year.now().getValue() || toYear > Year.now().getValue()) {
                JOptionPane.showMessageDialog(this,
                    "Năm không được lớn hơn năm hiện tại!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validation: Year range
            if (fromYear < 1900 || toYear > 2100) {
                JOptionPane.showMessageDialog(this,
                    "Năm phải nằm trong khoảng từ 1900 đến 2100!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validation: From year must be less than or equal to year
            if (fromYear > toYear) {
                JOptionPane.showMessageDialog(this,
                    "Năm bắt đầu không được lớn hơn năm kết thúc!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validation: Maximum range check
            if (toYear - fromYear > 50) {
                JOptionPane.showMessageDialog(this,
                    "Khoảng thời gian thống kê không được vượt quá 50 năm!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create new chart
            chartPanel.removeAll();
            chart = new Chart();
            chart.addLegend("Vốn", new Color(255, 193, 7));
            chart.addLegend("Doanh thu", new Color(13, 202, 240));
            chart.addLegend("Lợi nhuận", new Color(168, 85, 247));
            chartPanel.add(chart, BorderLayout.CENTER);
            
            // Load new data
            loadData(fromYear, toYear);
            
            // Refresh UI
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập năm hợp lệ!",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void handleRefresh() {
        txtFromYear.setText("2021");
        txtToYear.setText("2026");
        
        // Create new chart
        chartPanel.removeAll();
        chart = new Chart();
        chart.addLegend("Vốn", new Color(255, 193, 7));
        chart.addLegend("Doanh thu", new Color(13, 202, 240));
        chart.addLegend("Lợi nhuận", new Color(168, 85, 247));
        chartPanel.add(chart, BorderLayout.CENTER);
        
        loadDefaultData();
        
        // Refresh UI
        chartPanel.revalidate();
        chartPanel.repaint();
    }
    
    private void handleExportExcel() {
        if (currentData == null || currentData.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Không có dữ liệu để xuất!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            ExcelExporter.exportJTableToExcel(table);
            // JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!", 
                                        // "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

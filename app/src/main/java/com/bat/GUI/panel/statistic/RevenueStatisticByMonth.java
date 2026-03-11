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
import com.bat.DTO.Statistic.ThongKeTheoThangDTO;
import com.bat.GUI.component.chart.barchart.Chart;
import com.bat.GUI.component.chart.barchart.ModelChart;
import com.bat.utils.helper.ExcelExporter;

public class RevenueStatisticByMonth extends JPanel implements ActionListener {
    
    private final StatisticBLL statisticBLL;
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    
    private JTextField txtYear;
    private JButton btnStatistic;
    private JButton btnRefresh;
    private JButton btnExportExcel;
    
    private Chart chart;
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    
    private ArrayList<ThongKeTheoThangDTO> currentData;
    
    public RevenueStatisticByMonth(StatisticBLL statisticBLL) {
        this.statisticBLL = statisticBLL;
        initComponents();
        loadDefaultData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(228, 238, 255));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        add(createFilterPanel(), BorderLayout.NORTH);
        
        chartPanel = createChartPanel();
        add(chartPanel, BorderLayout.CENTER);
        
        add(createTablePanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createFilterPanel() { 
        JPanel pnl_top = new JPanel(new FlowLayout());
        JLabel lblChonNam = new JLabel("Chọn năm");

        txtYear = new JTextField(String.valueOf(Year.now().getValue()), 10);

        btnStatistic = new JButton("Thống kê");
        btnRefresh = new JButton("Làm mới");
        btnExportExcel = new JButton("Xuất excel");
        btnStatistic.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnExportExcel.addActionListener(this);

        pnl_top.add(lblChonNam);
        pnl_top.add(txtYear);
        pnl_top.add(btnStatistic);
        pnl_top.add(btnRefresh);
        pnl_top.add(btnExportExcel);

        return pnl_top;
    }
    
    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(0, 400));
        
        chart = new Chart();
        chart.addLegend("Chi phí", new Color(255, 193, 7));
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
        
        String[] columns = {"Tháng", "Chi phí", "Doanh thu", "Lợi nhuận"};
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
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
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
        loadData(currentYear);
    }
    
    private void loadData(int year) {
        try {
            currentData = statisticBLL.getDoanhThuTheoTungThang(year);
            
            tableModel.setRowCount(0);
            
            for (ThongKeTheoThangDTO dto : currentData) {
                String label = "Tháng " + dto.getThang();
                double[] values = {
                    dto.getChiphi().doubleValue(),
                    dto.getDoanhthu().doubleValue(),
                    dto.getLoinhuan().doubleValue()
                };
                chart.addData(new ModelChart(label, values));
                
                Object[] row = {
                    "Tháng " + dto.getThang(),
                    CURRENCY_FORMATTER.format(dto.getChiphi()),
                    CURRENCY_FORMATTER.format(dto.getDoanhthu()),
                    CURRENCY_FORMATTER.format(dto.getLoinhuan())
                };
                tableModel.addRow(row);
            }
            
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
            if (txtYear.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập năm cần thống kê!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int year = Integer.parseInt(txtYear.getText().trim());
            
            if (year < 1900 || year > 2100) {
                JOptionPane.showMessageDialog(this,
                    "Năm phải nằm trong khoảng từ 1900 đến 2100!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int currentYear = Year.now().getValue();
            if (year > currentYear) {
                JOptionPane.showMessageDialog(this,
                    "Năm thống kê không được vượt quá năm hiện tại (" + currentYear + ")!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            chartPanel.removeAll();
            chart = new Chart();
            chart.addLegend("Chi phí", new Color(255, 193, 7));
            chart.addLegend("Doanh thu", new Color(13, 202, 240));
            chart.addLegend("Lợi nhuận", new Color(168, 85, 247));
            chartPanel.add(chart, BorderLayout.CENTER);
            
            loadData(year);
            
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
        txtYear.setText(String.valueOf(Year.now().getValue()));
        
        chartPanel.removeAll();
        chart = new Chart();
        chart.addLegend("Chi phí", new Color(255, 193, 7));
        chart.addLegend("Doanh thu", new Color(13, 202, 240));
        chart.addLegend("Lợi nhuận", new Color(168, 85, 247));
        chartPanel.add(chart, BorderLayout.CENTER);
        
        loadDefaultData();
        
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

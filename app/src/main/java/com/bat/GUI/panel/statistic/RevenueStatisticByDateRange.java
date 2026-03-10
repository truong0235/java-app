package com.bat.GUI.panel.statistic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.bat.BLL.StatisticBLL;
import com.bat.DTO.Statistic.ThongKeTungNgayTrongThangDTO;
import com.bat.GUI.component.chart.barchart.Chart;
import com.bat.GUI.component.chart.barchart.ModelChart;
import com.bat.utils.helper.ExcelExporter;
import com.toedter.calendar.JDateChooser;

public class RevenueStatisticByDateRange extends JPanel implements ActionListener {
    
    private final StatisticBLL statisticBLL;
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    // UI Components
    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JButton btnStatistic;
    private JButton btnRefresh;
    private JButton btnExportExcel;
    
    private Chart chart;
    private JTable table;
    private DefaultTableModel tableModel;
    private JPanel chartPanel;
    
    private ArrayList<ThongKeTungNgayTrongThangDTO> currentData;
    
    public RevenueStatisticByDateRange(StatisticBLL statisticBLL) {
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
        JPanel pnl_top = new JPanel(new FlowLayout());
        JLabel lblTuNgay = new JLabel("Từ ngày");
        JLabel lblDenNgay = new JLabel("Đến ngày");

        dateFrom = new JDateChooser();
        dateFrom.setPreferredSize(new Dimension(150, 25));
        dateFrom.setDateFormatString("dd/MM/yyyy");
        
        dateTo = new JDateChooser();
        dateTo.setPreferredSize(new Dimension(150, 25));
        dateTo.setDateFormatString("dd/MM/yyyy");

        btnStatistic = new JButton("Thống kê");
        btnRefresh = new JButton("Làm mới");
        btnExportExcel = new JButton("Xuất excel");
        btnStatistic.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnExportExcel.addActionListener(this);

        pnl_top.add(lblTuNgay);
        pnl_top.add(dateFrom);
        pnl_top.add(lblDenNgay);
        pnl_top.add(dateTo);
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
        
        String[] columns = {"Ngày", "Chi phí", "Doanh thu", "Lợi nhuận"};
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
        // Set default dates (last 7 days)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        dateTo.setDate(cal.getTime());
        cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
        dateFrom.setDate(cal.getTime());
        
        loadData();
    }
    
    private void loadData() {
        try {
            if (dateFrom.getDate() == null || dateTo.getDate() == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ngày bắt đầu và ngày kết thúc!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String startDate = DATE_FORMAT.format(dateFrom.getDate());
            String endDate = DATE_FORMAT.format(dateTo.getDate());
            
            // Get data from BLL
            currentData = statisticBLL.getThongKeTuNgayDenNgay(startDate, endDate);
            
            // Clear table
            tableModel.setRowCount(0);
            
            // Update chart and table
            for (ThongKeTungNgayTrongThangDTO dto : currentData) {
                // Add to chart
                String label = DISPLAY_FORMAT.format(dto.getNgay());
                double[] values = {
                    dto.getChiphi().doubleValue(),
                    dto.getDoanhthu().doubleValue(),
                    dto.getLoinhuan().doubleValue()
                };
                chart.addData(new ModelChart(label, values));
                
                // Add to table
                Object[] row = {
                    DISPLAY_FORMAT.format(dto.getNgay()),
                    CURRENCY_FORMATTER.format(dto.getChiphi()),
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
            // Check null dates
            if (dateFrom.getDate() == null || dateTo.getDate() == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ngày bắt đầu và ngày kết thúc!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Date fromDate = dateFrom.getDate();
            Date toDate = dateTo.getDate();
            Date currentDate = new Date();
            
            // Validation: From date must be before or equal to date
            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(this,
                    "Ngày bắt đầu không được sau ngày kết thúc!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validation: Not future dates
            if (fromDate.after(currentDate)) {
                JOptionPane.showMessageDialog(this,
                    "Ngày bắt đầu không được là ngày trong tương lai!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (toDate.after(currentDate)) {
                JOptionPane.showMessageDialog(this,
                    "Ngày kết thúc không được là ngày trong tương lai!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Validation: Maximum range check (1 year = 365 days)
            long diffInMillis = toDate.getTime() - fromDate.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
            if (diffInDays > 365) {
                JOptionPane.showMessageDialog(this,
                    "Khoảng thời gian thống kê không được vượt quá 1 năm (365 ngày)!",
                    "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Create new chart
            chartPanel.removeAll();
            chart = new Chart();
            chart.addLegend("Chi phí", new Color(255, 193, 7));
            chart.addLegend("Doanh thu", new Color(13, 202, 240));
            chart.addLegend("Lợi nhuận", new Color(168, 85, 247));
            chartPanel.add(chart, BorderLayout.CENTER);
            
            // Load new data
            loadData();
            
            // Refresh UI
            chartPanel.revalidate();
            chartPanel.repaint();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi thống kê: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRefresh() {
        // Create new chart
        chartPanel.removeAll();
        chart = new Chart();
        chart.addLegend("Chi phí", new Color(255, 193, 7));
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
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

package com.bat.GUI.Panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.bat.GUI.Component.IntegratedSearch;
import com.bat.GUI.Component.MenuFunction;
import com.bat.GUI.Main;
import com.toedter.calendar.JDateChooser;

public class ImportManagement extends JPanel {
    IntegratedSearch searchPanel;
    MenuFunction menuFunction;
    JComboBox<String> providerCbx, userCbx;
    JDateChooser fromDateChooser, toDateChooser;
    public ImportManagement(Main main) {
        initComponent();
    }

    public void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Config cho trang quản lý phiếu nhập
        String[] importButtons = {"detail", "create", "update", "delete", "export"};
        
        String[] importSearchOptions = {"Tất cả", "Mã phiếu nhập", "Nhà cung cấp", "Nhân viên nhập"};

        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setBackground(new Color(228, 238, 255));
        menuBar.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Header panel với title và buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Title panel bên trái
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản lý phiếu nhập");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Danh sách phiếu nhập hàng hóa");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Tạo MenuFunction instance để truy cập buttons HashMap
        menuFunction = new MenuFunction(importButtons);
        for (String btnKey : importButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            btn.setActionCommand(btnKey);
            // btn.addActionListener(this);
        }
        
        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel ở dưới nếu có config
        if (importSearchOptions != null) {
            searchPanel = new IntegratedSearch(importSearchOptions);
            searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập mã phiếu, nhà cung cấp..."); 
            searchPanel.btnReset.setActionCommand("reset");
            // searchPanel.btnReset.addActionListener(this);
            // searchPanel.putClientProperty("FlatLaf.style", "arc: 15;");

            menuBar.add(searchPanel, BorderLayout.SOUTH);
        }

        this.add(menuBar, BorderLayout.NORTH);

        // Tạo table content cho phiếu nhập
        JPanel tablePanel = createImportTablePanel();
        this.add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createImportTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        // Tạo table với dữ liệu mẫu phiếu nhập
        String[] columns = {"Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Tổng tiền", "Trạng thái", "Ghi chú"};
        Object[][] data = {
            {"PN001", "15/01/2026", "Công ty ABC", "5.500.000đ", "Đã duyệt", "Nhập hàng tháng 1"},
            {"PN002", "18/01/2026", "Công ty XYZ", "3.200.000đ", "Chờ duyệt", "Nhập khẩn cấp"},  
            {"PN003", "22/01/2026", "Nhà cung cấp 123", "7.800.000đ", "Đã duyệt", "Hàng mới"},
            {"PN004", "25/01/2026", "Công ty DEF", "4.100.000đ", "Đã hủy", "Lỗi hàng hóa"},
            {"PN005", "28/01/2026", "Công ty GHI", "6.300.000đ", "Đã duyệt", "Nhập bổ sung"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        
        // Styling table
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        // Cell renderer for status column
        table.getColumnModel().getColumn(4).setCellRenderer(new ImportStatusCellRenderer());
        
        // Center align for some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        // Right align for money column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Custom renderer for status column
    private class ImportStatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if (status.equals("Đã duyệt")) {
                    setForeground(new Color(22, 163, 74));
                } else if (status.equals("Chờ duyệt")) {
                    setForeground(new Color(245, 158, 11));
                } else if (status.equals("Đã hủy")) {
                    setForeground(new Color(220, 38, 38));
                }
                setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}
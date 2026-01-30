package com.bat.GUI.Panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.bat.GUI.Component.MenuBarConfig;
import com.bat.GUI.Component.MenuFunction;
import com.bat.GUI.Main;

public class UserManagement extends JPanel {
    
    public UserManagement(Main main) {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(243, 244, 246));
        
        // Tạo config cho user management
        String[] userButtons = {"detail", "create", "update", "delete", "export"};
        
        MenuBarConfig userConfig = new MenuBarConfig(
            "Thông tin người dùng",
            "Thông tin cơ bản người dùng", 
            userButtons,
            new String[]{"Chọn Trạng thái", "Hoạt động", "Bị khoá"},
            "Nhập ID / Họ tên tài khoản"
        );
        
        JPanel menuBar = MenuFunction.createCompleteMenuBar(userConfig);
        this.add(menuBar, BorderLayout.NORTH);
        
        // Tạo table content
        JPanel tablePanel = createTablePanel();
        this.add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        // Tạo table với dữ liệu mẫu
        String[] columns = {"ID", "Hình ảnh", "Họ và tên", "Tên tài khoản", "Nhóm quyền", "Trạng thái"};
        Object[][] data = {
            {"001", "👤", "Nguyễn Văn A", "admin", "Quản trị viên", "Hoạt động"},
            {"002", "👤", "Trần Thị B", "user001", "Nhân viên", "Hoạt động"},  
            {"003", "👤", "Lê Văn C", "user002", "Nhân viên", "Bị khoá"},
            {"004", "👤", "Phạm Thị D", "user003", "Nhân viên", "Hoạt động"},
            {"005", "👤", "Hoàng Văn E", "user004", "Quản lý", "Hoạt động"}
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
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Cell renderer for status column
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());
        
        // Center align for ID and avatar columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Custom renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null) {
                String status = value.toString();
                if (status.equals("Hoạt động")) {
                    setForeground(new Color(22, 163, 74));
                    setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                } else if (status.equals("Bị khoá")) {
                    setForeground(new Color(220, 38, 38));
                    setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
                }
            }
            
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}
package com.bat.GUI.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class TopCustomerPanel extends JPanel {
    private final JTable customerTable;
    private final DefaultTableModel customerTblModel;
    
    public TopCustomerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Top Khách Hàng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"#", "Tên Khách Hàng", "Số Điện Thoại", "Số Đơn", "Tổng Tiền"};
        customerTblModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(customerTblModel);
        customerTable.setRowHeight(35);
        customerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customerTable.getTableHeader().setBackground(new Color(240, 240, 240));
        customerTable.getTableHeader().setForeground(new Color(51, 51, 51));
        customerTable.setSelectionBackground(new Color(230, 247, 255));
        customerTable.setGridColor(new Color(230, 230, 230));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        customerTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        customerTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        customerTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        customerTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setData(List<Object[]> data) {
        customerTblModel.setRowCount(0);
        int rank = 1;
        for (Object[] row : data) {
            Object[] rowData = new Object[5];
            rowData[0] = rank++;
            rowData[1] = row[0]; 
            rowData[2] = row[1]; 
            rowData[3] = row[2]; 
            rowData[4] = String.format("%,.0f₫", row[3]); 
            customerTblModel.addRow(rowData);
        }
    }
}

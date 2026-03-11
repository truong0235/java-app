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

public class TopProviderPanel extends JPanel {
    private final JTable providerTable;
    private final DefaultTableModel providerTableModel;
    
    public TopProviderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Top Nhà Cung Cấp");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"#", "Tên Nhà Cung Cấp", "Số Điện Thoại", "Số Đơn", "Tổng Tiền"};
        providerTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        providerTable = new JTable(providerTableModel);
        providerTable.setRowHeight(35);
        providerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        providerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        providerTable.getTableHeader().setBackground(new Color(240, 240, 240));
        providerTable.getTableHeader().setForeground(new Color(51, 51, 51));
        providerTable.setSelectionBackground(new Color(230, 247, 255));
        providerTable.setGridColor(new Color(230, 230, 230));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        providerTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        providerTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        providerTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        
        providerTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        providerTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        providerTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        providerTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        providerTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(providerTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setData(List<Object[]> data) {
        providerTableModel.setRowCount(0);
        int rank = 1;
        for (Object[] row : data) {
            Object[] rowData = new Object[5];
            rowData[0] = rank++;
            rowData[1] = row[0]; 
            rowData[2] = row[1]; 
            rowData[3] = row[2]; 
            rowData[4] = String.format("%,.0f₫", row[3]); 
            providerTableModel.addRow(rowData);
        }
    }
}

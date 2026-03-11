package com.bat.GUI.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.bat.BLL.LotBLL;
import com.bat.BLL.ProductBLL;
import com.bat.DTO.LotDTO;
import com.bat.DTO.LotTransactionDTO;
import com.bat.DTO.ProductDTO;

public class HistoryLotDialog extends JDialog implements ActionListener {
    private LotDTO lot;
    private LotBLL lotBLL = new LotBLL();
    private ProductBLL productBLL = new ProductBLL();
    
    private JTextField txtLotId, txtLotCode, txtProductName, txtCurrentQty, txtInitialQty;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnClose;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public HistoryLotDialog(JFrame parent, LotDTO lot) {
        super(parent, "Lịch sử thay đổi lô hàng", true);
        this.lot = lot;
        initComponents();
        loadData();
        this.setVisible(true);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(22, 122, 198));
        headerPanel.setPreferredSize(new Dimension(this.getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Lịch sử thay đổi lô hàng");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        JPanel infoPanel = createInfoPanel();
        
        JPanel tablePanel = createTablePanel();
        
        JPanel buttonPanel = createButtonPanel();
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JPanel lotIdPanel = createFieldPanel("Mã lô:");
        txtLotId = new JTextField();
        txtLotId.setEnabled(false);
        txtLotId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lotIdPanel.add(txtLotId);
        
        JPanel lotCodePanel = createFieldPanel("Mã lô TT:");
        txtLotCode = new JTextField();
        txtLotCode.setEnabled(false);
        txtLotCode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lotCodePanel.add(txtLotCode);
        
        JPanel productPanel = createFieldPanel("Tên sản phẩm:");
        txtProductName = new JTextField();
        txtProductName.setEnabled(false);
        txtProductName.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        productPanel.add(txtProductName);
        
        JPanel initialQtyPanel = createFieldPanel("Số lượng ban đầu:");
        txtInitialQty = new JTextField();
        txtInitialQty.setEnabled(false);
        txtInitialQty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        initialQtyPanel.add(txtInitialQty);
        
        JPanel currentQtyPanel = createFieldPanel("Số lượng hiện tại:");
        txtCurrentQty = new JTextField();
        txtCurrentQty.setEnabled(false);
        txtCurrentQty.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentQtyPanel.add(txtCurrentQty);
        
        panel.add(lotIdPanel);
        panel.add(lotCodePanel);
        panel.add(productPanel);
        panel.add(initialQtyPanel);
        panel.add(currentQtyPanel);
        
        return panel;
    }

    private JPanel createFieldPanel(String labelText) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 0, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(73, 80, 87));
        
        panel.add(label);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel tableTitle = new JLabel("Lịch sử giao dịch");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(new Color(33, 37, 41));
        tableTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        String[] columns = {"STT", "Loại giao dịch", "Thay đổi", "Số lượng sau", "Ngày giờ", "Mã tham chiếu"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  
        table.getColumnModel().getColumn(1).setPreferredWidth(120); 
        table.getColumnModel().getColumn(2).setPreferredWidth(100); 
        table.getColumnModel().getColumn(3).setPreferredWidth(120); 
        table.getColumnModel().getColumn(4).setPreferredWidth(150); 
        table.getColumnModel().getColumn(5).setPreferredWidth(100); 
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        DefaultTableCellRenderer changeRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(JLabel.CENTER);
                
                if (value != null && value instanceof String) {
                    String val = value.toString();
                    if (val.startsWith("+")) {
                        setForeground(new Color(22, 163, 74)); 
                    } else if (val.startsWith("-")) {
                        setForeground(new Color(220, 38, 38)); 
                    } else {
                        setForeground(table.getForeground());
                    }
                }
                
                if (!isSelected) {
                    setBackground(Color.WHITE);
                }
                
                return this;
            }
        };
        table.getColumnModel().getColumn(2).setCellRenderer(changeRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panel.setBackground(Color.WHITE);
        
        btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 35));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(this);
        
        panel.add(btnClose);
        
        return panel;
    }

    private void loadData() {
        txtLotId.setText(String.valueOf(lot.getLotId()));
        txtLotCode.setText(lot.getLotCode());
        
        ProductDTO product = productBLL.getProductById(lot.getProductId());
        if (product != null) {
            txtProductName.setText(product.getProductName());
        }
        
        txtInitialQty.setText(String.valueOf(lot.getInitialQuantity()));
        txtCurrentQty.setText(String.valueOf(lot.getQuantity()));
        
        loadTransactionHistory();
    }

    private void loadTransactionHistory() {
        tableModel.setRowCount(0);
        
        ArrayList<LotTransactionDTO> transactions = lotBLL.getLotTransactions(lot.getLotId());
        
        int stt = 1;
        for (LotTransactionDTO trans : transactions) {
            String transType = getTransactionTypeName(trans.getTransType());
            String quantityChange = formatQuantityChange(trans.getQuantityChange());
            String dateTime = trans.getTransDate().format(DATE_FORMATTER);
            
            Object[] rowData = {
                stt++,
                transType,
                quantityChange,
                trans.getQuantity(),
                dateTime,
                trans.getRefId()
            };
            
            tableModel.addRow(rowData);
        }
    }

    private String getTransactionTypeName(String type) {
        switch (type.toLowerCase()) {
            case "import":
                return "Nhập kho";
            case "export":
                return "Xuất kho";
            case "adjust":
                return "Điều chỉnh";
            default:
                return type;
        }
    }

    private String formatQuantityChange(int change) {
        if (change > 0) {
            return "+" + change;
        } else if (change < 0) {
            return String.valueOf(change);
        } else {
            return "0";
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
}

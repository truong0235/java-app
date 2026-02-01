package com.bat.GUI.Panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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

import com.bat.BLL.ImportBLL;
import com.bat.BLL.ProviderBLL;
import com.bat.BLL.UserBLL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.ProviderDTO;
import com.bat.DTO.UserDTO;
import com.bat.GUI.Component.IntegratedSearch;
import com.bat.GUI.Component.MenuFunction;
import com.bat.GUI.Main;
import com.toedter.calendar.JDateChooser;

public class Import extends JPanel implements ActionListener {
    UserBLL userBLL = new UserBLL();
    ProviderBLL providerBLL = new ProviderBLL();
    ImportBLL importBLL = new ImportBLL();

    DefaultTableModel tableModel;
    ArrayList<ImportDTO> importList;

    IntegratedSearch searchPanel;
    MenuFunction menuFunction;
    JComboBox<String> providerCbx, userCbx;
    JDateChooser fromDateChooser, toDateChooser;

    public Import(Main main) {
        initComponent();
        loadDataTable();
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
            btn.addActionListener(this);
        }
        
        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel ở dưới nếu có config
        if (importSearchOptions != null) {
            searchPanel = new IntegratedSearch(importSearchOptions);
            searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập mã phiếu, nhà cung cấp..."); 
            searchPanel.btnReset.setActionCommand("reset");
            searchPanel.btnReset.addActionListener(this);
            // searchPanel.putClientProperty("FlatLaf.style", "arc: 15;");

            menuBar.add(searchPanel, BorderLayout.SOUTH);
        }

        this.add(menuBar, BorderLayout.NORTH);


        
        // Tạo table content cho phiếu nhập
        JPanel tablePanel = createImportTablePanel();
        JPanel filterPanel = creatFilterPanel();
        this.add(filterPanel, BorderLayout.WEST);
        this.add(tablePanel, BorderLayout.CENTER);
    }
    
    @SuppressWarnings("unchecked")
    private JPanel creatFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,5,0));
        panel.setPreferredSize(new Dimension(250,0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 10, 250, 10));

        List<ProviderDTO> prdList = providerBLL.getProviderList();
        List<UserDTO> userList = userBLL.getUserList();

        JPanel prdPn = new JPanel();
        prdPn.setLayout(new GridLayout(2,1));
        prdPn.setBackground(Color.WHITE);
        JLabel prdLbl = new JLabel("Nhà cung cấp:");
        providerCbx = new JComboBox<>();
        providerCbx.addItem("Tất cả");
        for (ProviderDTO prd : prdList) {
            providerCbx.addItem(prd.getProviderName());
        }
        prdPn.add(prdLbl);
        prdPn.add(providerCbx);

        JPanel userPn = new JPanel();
        userPn.setLayout(new GridLayout(2,1));
        userPn.setBackground(Color.WHITE);
        JLabel userLbl = new JLabel("Nhân viên nhập:");
        userCbx = new JComboBox<>();
        userCbx.addItem("Tất cả");
        for (UserDTO user : userList) {
            userCbx.addItem(user.getUsername());
        }
        userPn.add(userLbl);
        userPn.add(userCbx);

        JPanel fromDatePn = new JPanel();
        fromDatePn.setLayout(new GridLayout(2,1));
        fromDatePn.setBackground(Color.WHITE);
        JLabel fromDateLbl = new JLabel("Từ ngày:");
        fromDateChooser = new JDateChooser();
        fromDatePn.add(fromDateLbl);
        fromDatePn.add(fromDateChooser);

        JPanel toDatePn = new JPanel();
        toDatePn.setLayout(new GridLayout(2,1));
        toDatePn.setBackground(Color.WHITE);
        JLabel toDateLbl = new JLabel("Đến ngày:");
        toDateChooser = new JDateChooser();
        toDatePn.add(toDateLbl);
        toDatePn.add(toDateChooser);

        panel.add(prdPn);
        panel.add(userPn);
        panel.add(fromDatePn);
        panel.add(toDatePn);

        return panel;
    }

    private JPanel createImportTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Tạo table với dữ liệu mẫu phiếu nhập
        String[] columns = {"Mã phiếu", "Nhà cung cấp", "Ngày nhập", "Nhân viên nhập" ,"Tổng tiền", "Trạng thái"};
        
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        
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
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Cell renderer for status column
        // table.getColumnModel().getColumn(4).setCellRenderer(new ImportStatusCellRenderer());
        
        // Center align for some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        // Right align for money column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Custom renderer for status column
    // private class ImportStatusCellRenderer extends DefaultTableCellRenderer {
    //     @Override
    //     public Component getTableCellRendererComponent(JTable table, Object value,
    //             boolean isSelected, boolean hasFocus, int row, int column) {
            
    //         super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
    //         if (value != null) {
    //             String status = value.toString();
    //             if (status.equals("Đã duyệt")) {
    //                 setForeground(new Color(22, 163, 74));
    //             } else if (status.equals("Chờ duyệt")) {
    //                 setForeground(new Color(245, 158, 11));
    //             } else if (status.equals("Đã hủy")) {
    //                 setForeground(new Color(220, 38, 38));
    //             }
    //             setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
    //         }
            
    //         setHorizontalAlignment(SwingConstants.CENTER);
    //         return this;
    //     }
    // }

    public void loadDataTable() {
        importList = importBLL.getImportList();
        tableModel.setRowCount(0);
        for (ImportDTO imp : importList) {
            Object[] rowData = {
                imp.getReceiptId(),
                providerBLL.getProviderNameById(imp.getProviderId()),
                imp.getCreatedDate(),
                userBLL.getUserNameById(imp.getUserId()),
                imp.getTotalPrice(),
                imp.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "create":
                System.out.println("Create button clicked");
                break;
            case "update":
                System.out.println("Update button clicked");
                break;
            case "delete":
                System.out.println("Delete button clicked");
                break;
            case "detail":
                System.out.println("Detail button clicked");
                break;
            case "export":
                System.out.println("Export button clicked");
                break;
            case "reset":
                System.out.println("Reset button clicked");
                resetFilterInputs();
                break;
            default:
                break;
        }
    }

    public void resetFilterInputs() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        providerCbx.setSelectedIndex(0);
        userCbx.setSelectedIndex(0);
        fromDateChooser.setDate(null);
        toDateChooser.setDate(null);
    }
}
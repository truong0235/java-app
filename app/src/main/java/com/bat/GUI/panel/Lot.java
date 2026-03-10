package com.bat.GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.bat.BLL.LotBLL;
import com.bat.BLL.ProductBLL;
import com.bat.BLL.ProviderBLL;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;
import com.bat.DTO.ProviderDTO;
import com.bat.GUI.Main;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;
import com.bat.GUI.dialog.HistoryLotDialog;
import com.bat.GUI.dialog.LotDetailDialog;
import com.bat.GUI.dialog.UpdateLotStatusDialog;
import com.bat.utils.helper.ExcelExporter;
import com.toedter.calendar.JDateChooser;

public class Lot extends JPanel implements ActionListener, ItemListener, KeyListener, PropertyChangeListener {
    // UserBLL userBLL = new UserBLL();
    ProviderBLL providerBLL = new ProviderBLL();
    LotBLL lotBLL = new LotBLL();
    ProductBLL productBLL = new ProductBLL();
    // ImportBLL importBLL = new ImportBLL();


    DefaultTableModel tableModel;
    JTable table;
    // ArrayList<ImportDTO> importList;
    ArrayList<LotDTO> lotList;
    

    IntegratedSearch searchPanel;
    MenuFunction menuFunction;
    JComboBox<String> providerCbx, productCbx;
    JDateChooser fromDateChooser, toDateChooser;
    Main main;
    
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    // private boolean isInitialized = false;

    public Lot(Main main) {
        this.main = main;
        initComponent();
        // importList = importBLL.getImportList();
        lotList = lotBLL.getLotList();
        loadDataTable(lotList);
    }

    public void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Config cho trang quản lý phiếu nhập
        String[] importButtons = {"detail", "history", "update", "export"};
        
        String[] importSearchOptions = {"Tất cả", "Mã lô", "Mã lô TT", "Tên sản phẩm"};

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
        
        JLabel titleLabel = new JLabel("Quản lý Lô hàng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Danh sách lô hàng");
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
            searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập mã lô hàng, ..."); 
            searchPanel.btnReset.setActionCommand("reset");
            searchPanel.btnReset.addActionListener(this);
            menuBar.add(searchPanel, BorderLayout.SOUTH);
            searchPanel.txtSearchForm.addKeyListener(this);
        }

        this.add(menuBar, BorderLayout.NORTH);
        
        // Tạo table content cho lô hàng
        JPanel tablePanel = createLotTablePanel();
        JPanel filterPanel = creatFilterPanel();
        this.add(filterPanel, BorderLayout.WEST);
        this.add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel creatFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,5,0));
        panel.setPreferredSize(new Dimension(250,0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 10, 250, 10));

        List<ProviderDTO> prdList = providerBLL.getProviderList();
        // List<UserDTO> userList = userBLL.getUserList();
        List<ProductDTO> productList = productBLL.getProductsList();

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

        JPanel productPn = new JPanel();
        productPn.setLayout(new GridLayout(2,1));
        productPn.setBackground(Color.WHITE);
        JLabel userLbl = new JLabel("Tên sản phẩm:");
        productCbx = new JComboBox<>();
        productCbx.addItem("Tất cả");
        // for (UserDTO user : userList) {
        //     userCbx.addItem(user.getUsername());
        // }
        for (ProductDTO prd : productList) {
            productCbx.addItem(prd.getProductName());
        }
        productPn.add(userLbl);
        productPn.add(productCbx);
        productCbx.addItemListener(this); 
        providerCbx.addItemListener(this);

        JPanel fromDatePn = new JPanel();
        fromDatePn.setLayout(new GridLayout(2,1));
        fromDatePn.setBackground(Color.WHITE);
        JLabel fromDateLbl = new JLabel("Từ ngày:");
        fromDateChooser = new JDateChooser();
        fromDateChooser.getDateEditor().getUiComponent().setFocusable(false);
        fromDatePn.add(fromDateLbl);
        fromDatePn.add(fromDateChooser);
        
        JPanel toDatePn = new JPanel();
        toDatePn.setLayout(new GridLayout(2,1));
        toDatePn.setBackground(Color.WHITE);
        JLabel toDateLbl = new JLabel("Đến ngày:");
        toDateChooser = new JDateChooser();
        toDateChooser.getDateEditor().getUiComponent().setFocusable(false);
        toDatePn.add(toDateLbl);
        toDatePn.add(toDateChooser);
        
        fromDateChooser.addPropertyChangeListener(this);
        toDateChooser.addPropertyChangeListener(this);


        panel.add(prdPn);
        panel.add(productPn);
        panel.add(fromDatePn);
        panel.add(toDatePn);

        return panel;
    }

    private JPanel createLotTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Tạo table với dữ liệu mẫu phiếu nhập
        String[] columns = {"Mã lô", "Mã lô TT", "Tên sản phẩm", "Số lượng BĐ" ,"Số lượng HT", "Giá nhập", "Trạng thái"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
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
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Cell renderer for status column
        // table.getColumnModel().getColumn(4).setCellRenderer(new ImportStatusCellRenderer());
        
        // Center align for some columns
        // DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        // centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        // table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        // table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        // table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        // table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        // Right align for money column
        // DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        // rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        // table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
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

    public void loadDataTable(ArrayList<LotDTO> lotData) {
        // importList = importBLL.getImportList();
        tableModel.setRowCount(0);
        for (LotDTO lot : lotData) {
            ProductDTO prd = productBLL.getProductById(lot.getProductId());
            String formattedPrice = lot.getImportPrice() != null ? CURRENCY_FORMATTER.format(lot.getImportPrice()) : "0 ₫";
            
            Object[] rowData = {
                lot.getLotId(),
                lot.getLotCode(),
                prd.getProductName(),
                lot.getInitialQuantity(),
                lot.getQuantity(),
                formattedPrice,
                lot.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    public int getRowSelected() {
        int index = table.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô hàng");
        }
        return index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
        //     case "create":
        //         AddImportDialog dialog = new AddImportDialog(main);
        //         dialog.setVisible(true);
        //         importList = importBLL.getImportList();
        //         loadDataTable(importList);
        //         break;
        //     case "update":
        //         System.out.println("Update button clicked");
        //         break;
        //     case "delete":
        //         // System.out.println("Delete button clicked");
        //         int selectedRow = getRowSelected();
        //         int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa phiếu nhập đã chọn?", "Xác nhận xóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        //         if (confirm == 0) {
        //             ImportDTO selectedImport = importList.get(selectedRow);
        //             if (importBLL.cancelImport(selectedImport.getReceiptId())) {
        //                 importList = importBLL.getImportList();
        //                 JOptionPane.showMessageDialog(null, "Xóa phiếu nhập thành công.");
        //                 loadDataTable(importList);
        //             }
        //             else {
        //                 JOptionPane.showMessageDialog(null, "Sản phẩm trong phiếu này đã được xuất kho, không thể xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        //             }
        //         }
        //         break;
        //     case "detail":
        //         int idx = getRowSelected();
        //         ReceiptDetailDialog detailDialog = new ReceiptDetailDialog(main, "Chi tiết phiếu nhập", importList.get(idx));
        //         // detailDialog.setVisible(true);
        //         break;
        //     case "export":
        //         System.out.println("Export button clicked");
        //         break;
            case "detail":
                int detailRow = getRowSelected();
                if (detailRow != -1) {
                    LotDTO detailLot = lotList.get(detailRow);
                    new LotDetailDialog(main, detailLot);
                }
                break;
            case "history":
                int selectedRow = getRowSelected();
                if (selectedRow != -1) {
                    LotDTO selectedLot = lotList.get(selectedRow);
                    new HistoryLotDialog(main, selectedLot);
                }
                break;
            case "update":
                int updateRow = getRowSelected();
                if (updateRow != -1) {
                    LotDTO updateLot = lotList.get(updateRow);
                    UpdateLotStatusDialog updateDialog = new UpdateLotStatusDialog(main, updateLot);
                    // Reload table if status was updated
                    if (updateDialog.isUpdated()) {
                        filter();
                    }
                }
                break;
            case "export":
                exportToExcel();
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
        productCbx.setSelectedIndex(0);
        toDateChooser.setDate(null);
        fromDateChooser.setDate(null);
    }

    public boolean validateFilterInputs(){
        // System.out.println();
        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();
        Date currentDate = new Date();

        if (fromDate != null && fromDate.after(currentDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày hiện tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            fromDateChooser.setDate(null);
            return false;
        }
        if (toDate != null && toDate.after(currentDate)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc không được lớn hơn ngày hiện tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            toDateChooser.setDate(null);
            return false;
        }
        if (fromDate != null && toDate != null && fromDate.after(toDate))
        {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            fromDateChooser.setDate(null);
            toDateChooser.setDate(null);
            return false;
        }
        if (fromDate == null && toDate != null)
        {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void filter() {
        if (validateFilterInputs()) {
            String searchTxt = searchPanel.txtSearchForm.getText().trim();            
            int prdId = providerCbx.getSelectedIndex() == 0 ? 0 : providerBLL.getPrdIdByIdx(providerCbx.getSelectedIndex() - 1);
            int productId = productCbx.getSelectedIndex() == 0 ? 0 : productBLL.getProductIdByIdx(productCbx.getSelectedIndex() - 1);
            int searchOpt = searchPanel.cbxChoose.getSelectedIndex();
            Date fromDate = fromDateChooser.getDate() == null ? null : fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate() == null ? null : toDateChooser.getDate();
            ArrayList<LotDTO> filteredLots = lotBLL.searchLots(searchTxt, prdId, productId, searchOpt, fromDate, toDate);
            loadDataTable(filteredLots);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getSource() == providerCbx || ie.getSource() == productCbx) {
            filter();
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        // throw new UnsupportedOperationException("Not supported yet.");
        filter();
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == fromDateChooser || pce.getSource() == toDateChooser) {
            filter();
        }
    }
    
    private void exportToExcel() {
        if (lotList == null || lotList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
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
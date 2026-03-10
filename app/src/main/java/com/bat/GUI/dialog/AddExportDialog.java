package com.bat.GUI.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.bat.BLL.CustomerBLL;
import com.bat.BLL.ExportBLL;
import com.bat.BLL.LotBLL;
import com.bat.BLL.ProductBLL;
import com.bat.DTO.CustomerDTO;
import com.bat.DTO.ExportLotDTO;
import com.bat.DTO.ExportReceiptDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;

public class AddExportDialog extends JDialog implements ActionListener {
    private ProductBLL productBLL = new ProductBLL();
    private CustomerBLL customerBLL = new CustomerBLL();
    private ExportBLL exportBLL = new ExportBLL();
    private LotBLL lotBLL = new LotBLL();
    private ArrayList<LotDTO> listLotsBaseOnProduct;

    private ArrayList<ProductDTO> productList;
    private ArrayList<ProductDTO> filteredProducts;
    private ArrayList<ExportLotDTO> selectedExportLots = new ArrayList<>();

    // Left panel - Product search
    private JTextField txtSearch;
    private JTable tblProducts;
    private DefaultTableModel productTableModel;

    // Right panel - Lot details
    private JTextField txtProductName, txtProductId, txtPublisher, txtPrice;
    private JComboBox<String> cbxCustomer, cbxLot;
    private JButton btnAdd, btnEdit, btnDelete;
    private JSpinner spnQuantity;
    
    // Bottom panel - Selected lots
    private JTable tblSelectedLots;
    private DefaultTableModel selectedLotsTableModel;
    private JLabel lblTotalPrice, labelQuantity;
    private JButton btnExport, btnCancel;

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));
    private int currentUserId;

    public AddExportDialog(JFrame parent, int userId) {
        super(parent, "Thêm phiếu xuất hàng", true);
        this.currentUserId = userId;
        productList = productBLL.getProductsList();
        filteredProducts = new ArrayList<>(productList);
        
        initComponents();
        setSize(1000, 700);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(0, 0, 0, 0));

        // Create main split panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(228, 238, 255));
        mainPanel.setBorder(new EmptyBorder(5,5,5,5));
        
        // Left-Right split
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(createProductSearchPanel());
        topPanel.add(createLotDetailsPanel());
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(createSelectedLotsPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createProductSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(Color.WHITE);

        // Search box
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm sản phẩm, mã sản phẩm...");
        txtSearch.setPreferredSize(new Dimension(0,30));
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterProducts();
            }
        });
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Product table
        String[] columns = {"Mã SP", "Tên sản phẩm", "SL"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProducts = new JTable(productTableModel);
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblProducts.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProducts.getSelectedRow() != -1) {
                tblSelectedLots.clearSelection();
                loadProductDetails();
                btnAdd.setEnabled(true);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });

        loadProductTable();

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblProducts), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLotDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(Color.WHITE);

        // Customer
        JPanel customerPn = new JPanel(new GridLayout(2, 1, 5, 5));
        customerPn.setBackground(Color.WHITE);
        customerPn.add(new JLabel("Khách hàng"));
        cbxCustomer = new JComboBox<>();
        ArrayList<CustomerDTO> customers = customerBLL.getCustomerList();
        for (CustomerDTO c : customers) {
            cbxCustomer.addItem(c.getFullName());
        }
        customerPn.add(cbxCustomer);

        // Product name
        JPanel prdNamePn = new JPanel(new GridLayout(2, 1, 5, 5));
        prdNamePn.setBackground(Color.WHITE);
        prdNamePn.add(new JLabel("Tên sản phẩm"));
        txtProductName = new JTextField();
        txtProductName.setEditable(false);
        prdNamePn.add(txtProductName);

        // Product ID & Lot Code
        JPanel idLotPn = new JPanel(new GridLayout(1, 2, 5, 5));
        idLotPn.setBackground(Color.WHITE);
        
        JPanel idPn = new JPanel(new GridLayout(2, 1, 5, 5));
        idPn.setBackground(Color.WHITE);
        idPn.add(new JLabel("Mã sản phẩm"));
        txtProductId = new JTextField();
        txtProductId.setEditable(false);
        idPn.add(txtProductId);
        
        JPanel lotPn = new JPanel(new GridLayout(2, 1, 5, 5));
        lotPn.setBackground(Color.WHITE);
        lotPn.add(new JLabel("Mã lô"));
        cbxLot = new JComboBox<>();
        cbxLot.addActionListener(this);
        lotPn.add(cbxLot);
        
        idLotPn.add(idPn);
        idLotPn.add(lotPn);

        // Publisher
        JPanel publisherPn = new JPanel(new GridLayout(2, 1, 5, 5));
        publisherPn.setBackground(Color.WHITE);
        publisherPn.add(new JLabel("Nhà xuất bản"));
        txtPublisher = new JTextField();
        txtPublisher.setEditable(false);
        publisherPn.add(txtPublisher);

        // Price & Quantity
        JPanel priceQtyPn = new JPanel(new GridLayout(1, 2, 5, 5));
        priceQtyPn.setBackground(Color.WHITE);
        
        JPanel pricePn = new JPanel(new GridLayout(2, 1, 5, 5));
        pricePn.setBackground(Color.WHITE);
        pricePn.add(new JLabel("Giá xuất"));
        txtPrice = new JTextField();
        txtPrice.setEditable(false);
        pricePn.add(txtPrice);
        
        JPanel qtyPn = new JPanel(new GridLayout(2, 1, 5, 5));
        qtyPn.setBackground(Color.WHITE);
        labelQuantity = new JLabel("Số lượng xuất");
        qtyPn.add(labelQuantity);
        spnQuantity = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        qtyPn.add(spnQuantity);
        
        priceQtyPn.add(pricePn);
        priceQtyPn.add(qtyPn);

        JPanel btnPn = new JPanel();
        btnPn.setLayout(new FlowLayout());
        btnPn.setBackground(Color.WHITE);

        btnAdd = new JButton("Thêm sản phẩm");
        btnAdd.setBackground(new Color(112,119,183));        
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.addActionListener(this);
        
        btnEdit = new JButton("Sửa sản phẩm");
        btnEdit.setBackground(new Color(112,119,183));
        btnEdit.setForeground(Color.WHITE);
        btnEdit.setFocusPainted(false);
        btnEdit.addActionListener(this);
        btnEdit.setEnabled(false);
        
        btnDelete = new JButton("Xóa sản phẩm");
        btnDelete.setBackground(new Color(112,119,183));        
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.addActionListener(this);
        btnDelete.setEnabled(false);
        
        btnPn.add(btnAdd);
        btnPn.add(btnEdit);
        btnPn.add(btnDelete);

        panel.add(customerPn);
        panel.add(prdNamePn);
        panel.add(idLotPn);
        panel.add(publisherPn);
        panel.add(priceQtyPn);
        panel.add(btnPn);

        return panel;
    }

    private JPanel createSelectedLotsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 250));

        // Table
        String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Mã lô", "Đơn giá", "Số lượng"};
        selectedLotsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSelectedLots = new JTable(selectedLotsTableModel);
        tblSelectedLots.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && tblSelectedLots.getSelectedRow() != -1) {
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
                btnAdd.setEnabled(false);
                tblProducts.clearSelection();
                loadExportDetails();
            }
        });
        
        // Bottom panel with total and buttons
        JPanel bottomPn = new JPanel(new BorderLayout());
        bottomPn.setBackground(Color.WHITE);
        
        JPanel totalPn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPn.setBackground(Color.WHITE);
        JLabel lblTotal = new JLabel("TỔNG TIỀN:");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalPrice = new JLabel("0đ");
        lblTotalPrice.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalPrice.setForeground(new Color(220, 53, 69));
        totalPn.add(lblTotal);
        totalPn.add(lblTotalPrice);
        
        JPanel buttonPn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPn.setBackground(Color.WHITE);
        
        btnExport = new JButton("Xuất hàng");
        btnExport.setBackground(new Color(40, 167, 69));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(this);
        
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this);
        
        buttonPn.add(btnExport);
        buttonPn.add(btnCancel);
        
        bottomPn.add(totalPn, BorderLayout.NORTH);
        bottomPn.add(buttonPn, BorderLayout.CENTER);

        panel.add(new JScrollPane(tblSelectedLots), BorderLayout.CENTER);
        panel.add(bottomPn, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProductTable() {
        productTableModel.setRowCount(0);
        for (ProductDTO product : filteredProducts) {
            productTableModel.addRow(new Object[]{
                product.getProductId(),
                product.getProductName(),
                product.getQuantity()
            });
        }
    }

    private void filterProducts() {
        String searchText = txtSearch.getText().trim().toLowerCase();
        filteredProducts.clear();
        
        if (searchText.isEmpty()) {
            filteredProducts.addAll(productList);
        } else {
            for (ProductDTO product : productList) {
                if (product.getProductName().toLowerCase().contains(searchText) ||
                    String.valueOf(product.getProductId()).contains(searchText)) {
                    filteredProducts.add(product);
                }
            }
        }
        loadProductTable();
    }

    private void loadProductDetails() {
        int selectedRow = tblProducts.getSelectedRow();
        if (selectedRow >= 0) {
            ProductDTO product = filteredProducts.get(selectedRow);
            txtProductId.setText(String.valueOf(product.getProductId()));
            txtProductName.setText(product.getProductName());
            txtPublisher.setText(product.getPublisher());
            txtPrice.setText(product.getPrice().toString());
            
            // Load lô hàng của sản phẩm
            cbxLot.removeAllItems();
            listLotsBaseOnProduct = lotBLL.getLotsByProductId(product.getProductId());
            for (LotDTO lot : listLotsBaseOnProduct) {
                if (lot.getQuantity() > 0 && !lot.getStatus().equals("Hết")) {
                    cbxLot.addItem(lot.getLotCode()); 
                }
            }
        }
    }

    private void loadExportDetails() {
        clearLotInputs();
        int selectedRow = tblSelectedLots.getSelectedRow();
        if (selectedRow >= 0) {
            ExportLotDTO exportLot = selectedExportLots.get(selectedRow);
            txtProductId.setText(String.valueOf(exportLot.getProductId()));
            ProductDTO product = productBLL.getProductById(exportLot.getProductId());
            txtProductName.setText(product.getProductName());
            txtPublisher.setText(product.getPublisher());
            txtPrice.setText(exportLot.getExportPrice().toString());
            cbxLot.setSelectedItem(exportLot.getLotCode());
            spnQuantity.setValue(exportLot.getQuantity());
        }
    }

    private boolean validateInput(String lotText, String oldLotText, String qtyText, String priceText) {
        if (txtProductId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
                
        if (lotText == null || lotText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mã lô!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        // Kiểm tra trùng lô trong danh sách đã chọn
        for (ExportLotDTO exportLot : selectedExportLots) {
            if (!oldLotText.isEmpty() && exportLot.getLotCode().equals(oldLotText)) 
                continue;
            if (exportLot.getLotCode().equals(lotText)) {
                JOptionPane.showMessageDialog(this, "Mã lô đã được thêm trong danh sách!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (qtyText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ số lượng và giá xuất!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }    
        
        int quantity = Integer.parseInt(qtyText);
        BigDecimal price = new BigDecimal(priceText);
       
        if (quantity <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng và giá xuất phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra số lượng tồn kho
        if (spnQuantity.getValue() != null && (int) spnQuantity.getValue() > 0) {
            for (LotDTO lot : listLotsBaseOnProduct) {
                if (cbxLot.getSelectedIndex() != -1 && lot.getLotCode().equals(cbxLot.getSelectedItem())) {
                    if (lot.getQuantity() < (int) spnQuantity.getValue()) {
                        JOptionPane.showMessageDialog(this, "Số lượng không được vượt quá " + lot.getQuantity(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        spnQuantity.setValue(lot.getQuantity());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void addLot() {
        try {
            String qtyText = spnQuantity.getValue().toString();
            String priceText = txtPrice.getText().trim();
            // String lotText = (String) cbxLot.getSelectedItem();
            String lotText = cbxLot.getSelectedIndex() != -1 ? (String) cbxLot.getSelectedItem() : "";

            // Validate inputs
            if (!validateInput(lotText, "", qtyText, priceText)) {
                return;
            }
            
            BigDecimal price = new BigDecimal(priceText);
            int quantity = Integer.parseInt(qtyText);
            int productId = Integer.parseInt(txtProductId.getText());
            String productName = txtProductName.getText();
            LotDTO lot = lotBLL.getLotByCode(lotText);
            
            // Create export lot
            ExportLotDTO exportLot = new ExportLotDTO();
            exportLot.setProductId(productId);
            exportLot.setProductName(productName);
            exportLot.setLotId(lot.getLotId());
            exportLot.setLotCode(lotText);
            exportLot.setQuantity(quantity);
            exportLot.setExportPrice(price);
            
            selectedExportLots.add(exportLot);
            
            // Add to table
            selectedLotsTableModel.addRow(new Object[]{
                selectedLotsTableModel.getRowCount() + 1,
                productId,
                productName,
                lotText,
                CURRENCY_FORMATTER.format(price),
                quantity
            });
            
            lblTotalPrice.setText(CURRENCY_FORMATTER.format(calTotalPrice()));
            clearLotInputs();
            tblProducts.clearSelection();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và giá xuất phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLotInputs() {
        cbxLot.setSelectedIndex(-1);
        spnQuantity.setValue(0);
        txtPrice.setText("");
        txtPublisher.setText("");
        txtProductId.setText("");
        txtProductName.setText("");
        labelQuantity.setText("Số lượng xuất");
    }
    
    private BigDecimal calTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExportLotDTO exportLot : selectedExportLots) {
            total = total.add(exportLot.getExportPrice().multiply(new BigDecimal(exportLot.getQuantity())));
        }
        return total;
    }

    private void exportProducts() {
        if (selectedExportLots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cbxCustomer.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal totalPrice = calTotalPrice();     
            
            // Lấy customerId
            ArrayList<CustomerDTO> customers = customerBLL.getCustomerList();
            int customerId = -1;
            if (cbxCustomer.getSelectedIndex() >= 0 && cbxCustomer.getSelectedIndex() < customers.size()) {
                customerId = customers.get(cbxCustomer.getSelectedIndex()).getCustomerId();
            }
            
            if (customerId == -1) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create export receipt
            ExportReceiptDTO exportDTO = new ExportReceiptDTO();
            exportDTO.setExport_date(LocalDateTime.now());
            exportDTO.setStatus(1);
            exportDTO.setUser_id(currentUserId);
            exportDTO.setTotal_price(totalPrice.intValue());
            exportDTO.setCustomer_id(customerId);
            
    
            boolean success = exportBLL.addExport(exportDTO, selectedExportLots);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Xuất hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Xuất hàng thất bại! Vui lòng kiểm tra lại số lượng tồn kho.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            addLot();
        } 
        else if (e.getSource() == btnExport) {
            exportProducts();
        }
        else if (e.getSource() == cbxLot) {
            if (cbxLot.getSelectedIndex() != -1) {
                for (LotDTO lot : listLotsBaseOnProduct) {
                    if (lot.getLotCode().equals(cbxLot.getSelectedItem())) {
                        labelQuantity.setText("Số lượng xuất (Tối đa: " + lot.getQuantity() + ")");
                        break;
                    }
                }
            }
        }
        else if (e.getSource() == btnCancel) {
            dispose();
        } 
        else if (e.getSource() == btnEdit) {
            int selectedRow = tblSelectedLots.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    String qtyText = spnQuantity.getValue().toString();
                    String priceText = txtPrice.getText().trim();
                    String lotText = (String) cbxLot.getSelectedItem();
                    ExportLotDTO exportLot = selectedExportLots.get(selectedRow);
                    
                    if (!validateInput(lotText, exportLot.getLotCode(), qtyText, priceText)) {
                        return;
                    }
                    
                    int quantity = Integer.parseInt(qtyText);
                    BigDecimal price = new BigDecimal(priceText);
                    exportLot.setQuantity(quantity);
                    exportLot.setExportPrice(price);
                    exportLot.setLotCode(lotText);

                    selectedLotsTableModel.setValueAt(lotText, selectedRow, 3);
                    selectedLotsTableModel.setValueAt(CURRENCY_FORMATTER.format(price), selectedRow, 4);
                    selectedLotsTableModel.setValueAt(quantity, selectedRow, 5);

                    lblTotalPrice.setText(CURRENCY_FORMATTER.format(calTotalPrice()));
                    clearLotInputs();

                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnAdd.setEnabled(true);
                    tblSelectedLots.clearSelection();

                } catch (NumberFormatException err) {
                    JOptionPane.showMessageDialog(this, "Số lượng và giá xuất phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (e.getSource() == btnDelete) {
            int selectedRow = tblSelectedLots.getSelectedRow();
            if (selectedRow >= 0) {
                selectedExportLots.remove(selectedRow);
                selectedLotsTableModel.removeRow(selectedRow);

                // Cập nhật lại số thứ tự
                for (int i = 0; i < selectedLotsTableModel.getRowCount(); i++) {
                    selectedLotsTableModel.setValueAt(i + 1, i, 0);
                }
                
                lblTotalPrice.setText(CURRENCY_FORMATTER.format(calTotalPrice()));
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                btnAdd.setEnabled(true);
                tblSelectedLots.clearSelection();
                clearLotInputs();
            }
        }
    }
}

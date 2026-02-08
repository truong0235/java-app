package com.bat.GUI.Dialog;

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
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.bat.BLL.ImportBLL;
import com.bat.BLL.LotBLL;
import com.bat.BLL.ProviderBLL;
import com.bat.DAL.ProductDAL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;
import com.bat.DTO.ProviderDTO;

public class AddImportDialog extends JDialog implements ActionListener {
    private ProductDAL productDAL = new ProductDAL();
    private ProviderBLL providerBLL = new ProviderBLL();
    private ImportBLL importBLL = new ImportBLL();
    private LotBLL lotBLL = new LotBLL();

    private ArrayList<ProductDTO> productList;
    private ArrayList<ProductDTO> filteredProducts;
    private ArrayList<LotDTO> selectedLots = new ArrayList<>();

    // Left panel - Product search
    private JTextField txtSearch;
    private JTable tblProducts;
    private DefaultTableModel productTableModel;

    // Right panel - Lot details
    private JTextField txtProductName, txtProductId, txtLot, txtPublisher, txtQuantity, txtPrice;
    private JComboBox<String> cbxProvider;
    private JButton btnAdd, btnEdit, btnDelete;

    // Bottom panel - Selected lots
    private JTable tblSelectedLots;
    private DefaultTableModel selectedLotsTableModel;
    private JLabel lblTotalPrice;
    private JButton btnImport, btnCancel;

    private int USERID = 1;

    public AddImportDialog(JFrame parent) {
        super(parent, "Thêm phiếu nhập", true);
        // this.currentUserId = userId;
        productList = productDAL.getProducts();
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
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(100);
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

        // Provider
        JPanel providerPn = new JPanel(new GridLayout(2, 1, 5, 5));
        providerPn.setBackground(Color.WHITE);
        providerPn.add(new JLabel("Nhà cung cấp"));
        cbxProvider = new JComboBox<>();
        ArrayList<ProviderDTO> providers = providerBLL.getProviderList();
        for (ProviderDTO p : providers) {
            cbxProvider.addItem(p.getProviderName());
        }
        providerPn.add(cbxProvider);

        // Product name
        JPanel prdNamePn = new JPanel(new GridLayout(2, 1, 5, 5));
        prdNamePn.setBackground(Color.WHITE);
        prdNamePn.add(new JLabel("Tên sản phẩm"));
        txtProductName = new JTextField();
        txtProductName.setEditable(false);
        prdNamePn.add(txtProductName);

        // Product ID & ISBN
        JPanel idIsbnPn = new JPanel(new GridLayout(1, 2, 5, 5));
        idIsbnPn.setBackground(Color.WHITE);
        
        JPanel idPn = new JPanel(new GridLayout(2, 1, 5, 5));
        idPn.setBackground(Color.WHITE);
        idPn.add(new JLabel("Mã sản phẩm"));
        txtProductId = new JTextField();
        txtProductId.setEditable(false);
        idPn.add(txtProductId);
        
        JPanel isbnPn = new JPanel(new GridLayout(2, 1, 5, 5));
        isbnPn.setBackground(Color.WHITE);
        isbnPn.add(new JLabel("Mã lô"));
        txtLot = new JTextField();
        isbnPn.add(txtLot);
        
        idIsbnPn.add(idPn);
        idIsbnPn.add(isbnPn);

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
        pricePn.add(new JLabel("Giá nhập"));
        txtPrice = new JTextField();
        pricePn.add(txtPrice);
        
        JPanel qtyPn = new JPanel(new GridLayout(2, 1, 5, 5));
        qtyPn.setBackground(Color.WHITE);
        qtyPn.add(new JLabel("Số lượng"));
        txtQuantity = new JTextField();
        qtyPn.add(txtQuantity);
        
        priceQtyPn.add(pricePn);
        priceQtyPn.add(qtyPn);

        JPanel btnPn = new JPanel();
        btnPn.setLayout(new FlowLayout());
        btnPn.setBackground(Color.WHITE);
        // btnPn.setBorder(new EmptyBorder(5,0,5, 0));

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

        panel.add(providerPn);
        panel.add(prdNamePn);
        panel.add(idIsbnPn);
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
        String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Đơn giá", "Số lượng"};
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
                loadImportDetails();
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
        
        btnImport = new JButton("Nhập hàng");
        btnImport.setBackground(new Color(40, 167, 69));
        btnImport.setForeground(Color.WHITE);
        btnImport.setFocusPainted(false);
        btnImport.addActionListener(this);
        
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(this);
        
        buttonPn.add(btnImport);
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
            txtLot.setText("");
            txtQuantity.setText("");
        }
    }

    private void loadImportDetails() {
        int selectedRow = tblSelectedLots.getSelectedRow();
        if (selectedRow >= 0) {
            LotDTO lot = selectedLots.get(selectedRow);
            txtProductId.setText(String.valueOf(lot.getProductId()));
            ProductDTO product = productDAL.getProductById(lot.getProductId());
            txtProductName.setText(product.getProductName());
            txtPublisher.setText(product.getPublisher());
            txtPrice.setText(lot.getImportPrice().toString());
            txtLot.setText(lot.getLotCode());
            txtQuantity.setText(String.valueOf(lot.getQuantity()));
        }
    }

    public boolean validateInput(String lotText, String oldLotText,String qtyText, String priceText, int quantity, BigDecimal price) {
        if (txtProductId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
                
        if (lotText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã lô!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if (lotBLL.isLotCodeExist(lotText)) {
            JOptionPane.showMessageDialog(this, "Mã lô đã tồn tại! Vui lòng nhập mã lô khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else {
            for (LotDTO lot : selectedLots) {
                if (!oldLotText.isEmpty() && lot.getLotCode().equals(oldLotText)) 
                    continue;
                if (lot.getLotCode().equals(lotText)) {
                    JOptionPane.showMessageDialog(this, "Mã lô đã được thêm trong danh sách! Vui lòng nhập mã lô khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }


        if (qtyText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ số lượng và giá nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }       
       
        if (quantity <= 0 || price.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng và giá nhập phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void addLot() {
        try {
            String qtyText = txtQuantity.getText().trim();
            String priceText = txtPrice.getText().trim();
            String lotText = txtLot.getText().trim();
            int quantity = Integer.parseInt(qtyText);
            BigDecimal price = new BigDecimal(priceText);
            // Validate inputs
            if (!validateInput(lotText, "", qtyText, priceText, quantity, price)) {
                return;
            }
                   
            int productId = Integer.parseInt(txtProductId.getText());
            String productName = txtProductName.getText();
            
            // Create lot
            LotDTO lot = new LotDTO();
            lot.setProductId(productId);
            lot.setLotCode(lotText);
            lot.setQuantity(quantity);
            lot.setInitialQuantity(quantity);
            lot.setImportPrice(price);
            lot.setImportDate(LocalDateTime.now());
            lot.setPrintYear(LocalDateTime.now().getYear());
            lot.setStatus("Con");
            
            selectedLots.add(lot);
            
            // Add to table
            selectedLotsTableModel.addRow(new Object[]{
                selectedLotsTableModel.getRowCount() + 1,
                productId,
                productName,
                price + "đ",
                quantity
            });
            
            lblTotalPrice.setText(calTotalPrice() + "đ");
            clearLotInputs();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng và giá nhập phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLotInputs() {
        txtLot.setText("");
        txtQuantity.setText("");
    }
    
    private BigDecimal calTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        for (LotDTO lot : selectedLots) {
            total = total.add(lot.getImportPrice().multiply(new BigDecimal(lot.getQuantity())));
        }
        return total;
    }

    private void importProducts() {
        if (selectedLots.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cbxProvider.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            BigDecimal totalPrice = calTotalPrice();     
            int providerId = providerBLL.getPrdIdByIdx(cbxProvider.getSelectedIndex());
            
            // Create import receipt
            ImportDTO importDTO = new ImportDTO();
            importDTO.setProviderId(providerId);
            importDTO.setUserId(USERID);
            importDTO.setTotalPrice(totalPrice);
            importDTO.setStatus(1);
            importDTO.setCreatedDate(LocalDateTime.now());
            System.out.println(importDTO);
            
            // Save to database
            boolean success = importBLL.addImport(importDTO, selectedLots);
            // boolean success = false;
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Nhập hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Nhập hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        else if (e.getSource() == btnImport) {
            importProducts();
        }
        else if (e.getSource() == btnCancel) {
            dispose();
        } 
        else if (e.getSource() == btnEdit) {
            int selectedRow = tblSelectedLots.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int quantity = Integer.parseInt(txtQuantity.getText().trim());
                    BigDecimal price = new BigDecimal(txtPrice.getText().trim());
                    String qtyText = txtQuantity.getText().trim();
                    String priceText = txtPrice.getText().trim();
                    String lotText = txtLot.getText().trim();
                    LotDTO lot = selectedLots.get(selectedRow);
                    if (!validateInput(lotText, lot.getLotCode(), qtyText, priceText, quantity, price )) {
                        return;
                    }

                    lot.setQuantity(quantity);
                    lot.setImportPrice(price);
                    lot.setLotCode(lotText);

                    selectedLotsTableModel.setValueAt(price + "đ", selectedRow, 3);
                    selectedLotsTableModel.setValueAt(quantity, selectedRow, 4);

                    lblTotalPrice.setText(calTotalPrice() + "đ");
                    clearLotInputs();

                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnAdd.setEnabled(true);
                    tblSelectedLots.clearSelection();

                } catch (NumberFormatException err) {
                    JOptionPane.showMessageDialog(this, "Số lượng và giá nhập phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (e.getSource() == btnDelete) {
            int selectedRow = tblSelectedLots.getSelectedRow();
            if (selectedRow >= 0) {
                selectedLots.remove(selectedRow);
                selectedLotsTableModel.removeRow(selectedRow);

                for (int i = 0; i < selectedLotsTableModel.getRowCount(); i++) {
                    selectedLotsTableModel.setValueAt(i + 1, i, 0);
                }
                lblTotalPrice.setText(calTotalPrice() + "đ");
                clearLotInputs();
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                btnAdd.setEnabled(true);
                tblSelectedLots.clearSelection();
            }
        }
        
    }
}

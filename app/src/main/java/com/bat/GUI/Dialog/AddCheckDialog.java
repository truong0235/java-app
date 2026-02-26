package com.bat.GUI.Dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import com.bat.BLL.InventoryCheckBLL;
import com.bat.BLL.LotBLL;
import com.bat.BLL.ProductBLL;
import com.bat.BLL.ProviderBLL;
import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.InventoryCheckDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;

public class AddCheckDialog extends JDialog implements ActionListener { // thêm một ô só lượng ban đầu 
    private ProductBLL productBLL = new ProductBLL();
    private ProviderBLL providerBLL = new ProviderBLL();
    private ImportBLL importBLL = new ImportBLL();
    private InventoryCheckBLL checkBLL = new InventoryCheckBLL();
    private LotBLL lotBLL = new LotBLL();

    private ArrayList<ProductDTO> productList;
    private ArrayList<ProductDTO> filteredProducts;
    private ArrayList<CheckDetailDTO> selectedDetails = new ArrayList<>();
    private ArrayList<LotDTO> lotList;
    private ArrayList<LotDTO> filteredLots = new ArrayList<>();

    // Left panel - Product search
    private JTextField txtPrSearch;
    private JTable tblProducts;
    private DefaultTableModel productTableModel;

    private JTextField txtLotSearch;
    private JTable tblLots;
    private DefaultTableModel lotTableModel;

    // Right panel - Lot details
    private JTextField txtProductName, txtProductId, txtLot, txtQuantity, txtSysQty;
    private JButton btnAdd, btnEdit, btnDelete;

    // Bottom panel - Selected lots
    private JTable tblSelectedDetails;
    private DefaultTableModel selectedDetailsTableModel;
    // private JLabel lblTotalPrice;
    private JButton btnImport, btnCancel;

    private int USERID = 1;

    public AddCheckDialog(JFrame parent) {
        super(parent, "Thêm phiếu nhập", true);
        // this.currentUserId = userId;
        productList = productBLL.getProductsList();
        // lotList = lotBLL.getLots();
        // filteredLots = new ArrayList<>(lotList);
        filteredLots = new ArrayList<>();
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
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(createProductSearchPanel());
        topPanel.add(createLotSearchPanel());
        topPanel.add(createCheckDetailsPanel());
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(createSelectedDetailsPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createProductSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(Color.WHITE);

        // Search box
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(Color.WHITE);
        txtPrSearch = new JTextField();
        txtPrSearch.putClientProperty("JTextField.placeholderText", "Tìm sản phẩm, mã sản phẩm...");
        txtPrSearch.setPreferredSize(new Dimension(0,30));
        txtPrSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterProducts();
            }
        });
        searchPanel.add(txtPrSearch, BorderLayout.CENTER);

        // Product table
        String[] columns = {"Mã SP", "Tên sản phẩm"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProducts = new JTable(productTableModel);
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblProducts.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblProducts.getSelectedRow() != -1) {
                tblSelectedDetails.clearSelection();
                tblLots.clearSelection();
                txtLotSearch.setText("");
                lotList = lotBLL.getLotsByProductId(filteredProducts.get(tblProducts.getSelectedRow()).getProductId());
                filteredLots = new ArrayList<>(lotList);
                loadLotTable();
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

    private JPanel createLotSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(Color.WHITE);

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBackground(Color.WHITE);
        txtLotSearch = new JTextField();
        txtLotSearch.putClientProperty("JTextField.placeholderText", "Nhập mã lô thực tế...");
        txtLotSearch.setPreferredSize(new Dimension(0,30));
        txtLotSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterLots();
            }
        });
        searchPanel.add(txtLotSearch, BorderLayout.CENTER);

        String[] columns = {"Mã lô", "Mã lô TT", "Số lượng"};
        lotTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }   
        };
        tblLots = new JTable(lotTableModel);

        tblLots.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && tblLots.getSelectedRow() != -1) {
                tblSelectedDetails.clearSelection();
                loadLotDetails();
                btnAdd.setEnabled(true);
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblLots), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCheckDetailsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5,5,5,5));
        panel.setBackground(Color.WHITE);


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
        
        JPanel PrdIdPn = new JPanel(new GridLayout(2, 1, 5, 5));
        PrdIdPn.setBackground(Color.WHITE);
        PrdIdPn.add(new JLabel("Mã sản phẩm"));
        txtProductId = new JTextField();
        txtProductId.setEditable(false);
        PrdIdPn.add(txtProductId);
        
        JPanel isbnPn = new JPanel(new GridLayout(2, 1, 5, 5));
        isbnPn.setBackground(Color.WHITE);
        isbnPn.add(new JLabel("Mã lô"));
        txtLot = new JTextField();
        txtLot.setEditable(false);
        isbnPn.add(txtLot);
        
        idIsbnPn.add(PrdIdPn);
        idIsbnPn.add(isbnPn);

        // Price & Quantity
        JPanel qtyPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        qtyPanel.setBackground(Color.WHITE);
        
        JPanel sysQtyPn = new JPanel(new GridLayout(2, 1, 5, 5));
        sysQtyPn.setBackground(Color.WHITE);
        sysQtyPn.add(new JLabel("Số lượng HT"));
        txtSysQty = new JTextField();
        txtSysQty.setEditable(false);
        sysQtyPn.add(txtSysQty);
        
        JPanel qtyPn = new JPanel(new GridLayout(2, 1, 5, 5));
        qtyPn.setBackground(Color.WHITE);
        qtyPn.add(new JLabel("Số lượng TT"));
        txtQuantity = new JTextField();
        qtyPn.add(txtQuantity);
        
        qtyPanel.add(sysQtyPn);
        qtyPanel.add(qtyPn);

        JPanel btnPn = new JPanel();
        btnPn.setLayout(new FlowLayout());
        btnPn.setBackground(Color.WHITE);
        btnPn.setBorder(new EmptyBorder(80,0,5, 0));

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

        panel.add(prdNamePn);
        panel.add(idIsbnPn);
        panel.add(qtyPanel);
        panel.add(btnPn);

        return panel;
    }

    private JPanel createSelectedDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, 250));

        // Table
        String[] columns = {"STT", "Mã SP", "Tên sản phẩm", "Mã lô","Số lượng HT", "Số lượng TT"};
        selectedDetailsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSelectedDetails = new JTable(selectedDetailsTableModel);
        tblSelectedDetails.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && tblSelectedDetails.getSelectedRow() != -1) {
                btnEdit.setEnabled(true);
                btnDelete.setEnabled(true);
                btnAdd.setEnabled(false);
                tblProducts.clearSelection();
                tblLots.clearSelection();
                loadCheckDetails();
            }          
        });
        
        // Bottom panel with total and buttons
        JPanel bottomPn = new JPanel(new BorderLayout());
        bottomPn.setBackground(Color.WHITE);
        
        JPanel buttonPn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPn.setBackground(Color.WHITE);
        
        btnImport = new JButton("Kiểm kê");
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
        
        bottomPn.add(buttonPn, BorderLayout.CENTER);

        panel.add(new JScrollPane(tblSelectedDetails), BorderLayout.CENTER);
        panel.add(bottomPn, BorderLayout.SOUTH);

        return panel;
    }

    private void loadProductTable() {
        productTableModel.setRowCount(0);
        for (ProductDTO product : filteredProducts) {
            productTableModel.addRow(new Object[]{
                product.getProductId(),
                product.getProductName(),
            });
        }
    }

    private void loadLotTable() {
        lotTableModel.setRowCount(0);
        for (LotDTO lot : filteredLots) {
            lotTableModel.addRow(new Object[]{
                lot.getLotId(),
                lot.getLotCode(),
                lot.getQuantity(),
            });
        }
    }

    private void filterProducts() {
        String searchText = txtPrSearch.getText().trim().toLowerCase();
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

    private void filterLots() {
        String searchText = txtLotSearch.getText().trim().toLowerCase();
        filteredLots.clear();
        
        if (searchText.isEmpty()) {
            filteredLots.addAll(lotList);
        } else {
            for (LotDTO lot : lotList) {
                if (lot.getLotCode().toLowerCase().contains(searchText)) {
                    filteredLots.add(lot);
                }
            }
        }
        loadLotTable();
        
    }

    private void loadLotDetails() {
        int selectedLot = tblLots.getSelectedRow();
        int selectedPrd = tblProducts.getSelectedRow();
        if (selectedLot >= 0) {
            ProductDTO product = filteredProducts.get(selectedPrd);
            LotDTO lot = filteredLots.get(selectedLot);
            txtProductId.setText(String.valueOf(product.getProductId()));
            txtProductName.setText(product.getProductName());
            txtSysQty.setText(String.valueOf(lot.getQuantity()));
            txtLot.setText(lot.getLotCode());
            txtQuantity.setText("");
        }
    }

    private void loadCheckDetails() {
        int selectedRow = tblSelectedDetails.getSelectedRow();
        if (selectedRow >= 0) {
            CheckDetailDTO detail = selectedDetails.get(selectedRow);
            ProductDTO product = productBLL.getProductByLotId(detail.getLotId());
            LotDTO lot = lotBLL.getLotById(detail.getLotId());
            System.out.println(lot.toString());

            txtProductId.setText(String.valueOf(product.getProductId()));
            txtProductName.setText(product.getProductName());
            txtSysQty.setText(String.valueOf(detail.getSystemQuantity()));
            txtLot.setText(lot.getLotCode());
            txtQuantity.setText(String.valueOf(detail.getActualQuantity()));
        }
    }

    public boolean validateInput(String lotText, String qtyText, String sysQtyText, int initQty) {
        if (txtProductId.getText().trim().isEmpty() || lotText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm và lô hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ số lượng thực tế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        } 
        int quantity = Integer.parseInt(qtyText);      
       
        if (quantity < 0) {
            JOptionPane.showMessageDialog(this, "Số lượng thực tế không được âm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        else if (quantity > initQty) {
            JOptionPane.showMessageDialog(this, "Số lượng thực tế không được lớn hơn số lượng lúc mới nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void addCheckDetails() {
        try {
            LotDTO selectedLot = filteredLots.get(tblLots.getSelectedRow());
            String qtyText = txtQuantity.getText().trim();
            String sysQtyText = txtSysQty.getText().trim();
            String lotText = txtLot.getText().trim();
            int initQty = selectedLot.getInitialQuantity();
            
            // Validate inputs
            if (!validateInput(lotText, qtyText, sysQtyText, initQty)) {
                return;
            }
            
            int quantity = Integer.parseInt(qtyText);
            int productId = Integer.parseInt(txtProductId.getText());
            String productName = txtProductName.getText();

            CheckDetailDTO detail = new CheckDetailDTO();
            detail.setLotId(selectedLot.getLotId());
            detail.setActualQuantity(quantity);
            detail.setSystemQuantity(Integer.parseInt(sysQtyText));
            detail.setDifference(quantity - initQty);
            
            selectedDetails.add(detail);
            
            // Add to table
            selectedDetailsTableModel.addRow(new Object[]{
                selectedDetailsTableModel.getRowCount() + 1,
                productId,
                productName,
                selectedLot.getLotCode(),
                sysQtyText,
                quantity
            });
            tblProducts.clearSelection();
            tblLots.clearSelection();
            clearLotInputs();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLotInputs() {
        txtProductId.setText("");
        txtProductName.setText("");
        txtSysQty.setText("");
        txtLot.setText("");
        txtQuantity.setText("");
    }

    private void importChecks() {
        if (selectedDetails.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một sản phẩm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            
            // Create import receipt
            InventoryCheckDTO checkDTO = new InventoryCheckDTO();
            checkDTO.setUserId(USERID);
            checkDTO.setStatus(1);
            checkDTO.setCheckDate(LocalDateTime.now());
            System.out.println(checkDTO);
            
            // Save to database
            boolean success = checkBLL.addCheck(checkDTO, selectedDetails);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Kiểm kê thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Kiểm kê thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            addCheckDetails();
        } 
        else if (e.getSource() == btnImport) {
            importChecks();
        }
        else if (e.getSource() == btnCancel) {
            dispose();
        } 
        else if (e.getSource() == btnEdit) {
            int selectedRow = tblSelectedDetails.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    // LotDTO selectedLot = filteredLots.get(tblSelectedDetails.getSelectedRow());
                    CheckDetailDTO selectedDetail = selectedDetails.get(selectedRow);
                    LotDTO selectedLot = lotBLL.getLotById(selectedDetail.getLotId());
                    String qtyText = txtQuantity.getText().trim();
                    String sysQtyText = txtSysQty.getText().trim();
                    String lotText = txtLot.getText().trim();
                    int initQty = selectedLot.getInitialQuantity();
                    
                    // Validate inputs
                    if (!validateInput(lotText, qtyText, sysQtyText, initQty)) {
                        return;
                    }
                    
                    int quantity = Integer.parseInt(qtyText);

                    selectedDetail.setActualQuantity(quantity);
                    selectedDetailsTableModel.setValueAt(quantity, selectedRow, 5);
                    clearLotInputs();

                    btnEdit.setEnabled(false);
                    btnDelete.setEnabled(false);
                    btnAdd.setEnabled(true);
                    tblSelectedDetails.clearSelection();

                } catch (NumberFormatException err) {
                    JOptionPane.showMessageDialog(this, "Số lượng và giá nhập phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        else if (e.getSource() == btnDelete) {
            int selectedRow = tblSelectedDetails.getSelectedRow();
            if (selectedRow >= 0) {
                selectedDetails.remove(selectedRow);
                selectedDetailsTableModel.removeRow(selectedRow);

                for (int i = 0; i < selectedDetailsTableModel.getRowCount(); i++) {
                    selectedDetailsTableModel.setValueAt(i + 1, i, 0);
                }
                clearLotInputs();
                btnEdit.setEnabled(false);
                btnDelete.setEnabled(false);
                btnAdd.setEnabled(true);
                tblSelectedDetails.clearSelection();
            }
        }
        
    }
}

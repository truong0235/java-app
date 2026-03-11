package com.bat.GUI.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.bat.BLL.ImportBLL;
import com.bat.BLL.ProductBLL;
import com.bat.BLL.ProviderBLL;
import com.bat.BLL.UserBLL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;
import com.bat.DTO.ProviderDTO;

public class LotDetailDialog extends JDialog implements ActionListener {
    private final LotDTO lot;
    private final ProductBLL productBLL = new ProductBLL();
    private final ImportBLL importBLL = new ImportBLL();
    private final ProviderBLL providerBLL = new ProviderBLL();
    private final UserBLL userBLL = new UserBLL();
    
    private JTextField txtLotId, txtLotCode, txtProductName, txtImportId;
    private JTextField txtProvider, txtUser, txtImportDate;
    private JTextField txtInitialQty, txtCurrentQty, txtImportPrice;
    private JTextField txtPrintYear, txtStatus;
    private JButton btnClose, btnHistory;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public LotDetailDialog(JFrame parent, LotDTO lot) {
        super(parent, "Chi tiết lô hàng", true);
        this.lot = lot;
        initComponents();
        loadData();
        this.setVisible(true);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);
        
        JPanel headerPanel = createHeaderPanel();
        
        JScrollPane scrollPane = new JScrollPane(createMainPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel buttonPanel = createButtonPanel();
        
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(22, 122, 198));
        panel.setPreferredSize(new Dimension(this.getWidth(), 60));
        
        JLabel titleLabel = new JLabel("Chi tiết lô hàng");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        panel.add(titleLabel, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        
        JPanel lotInfoPanel = createSectionPanel("Thông tin lô hàng", createLotInfoContent());
        lotInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        container.add(lotInfoPanel);
        container.add(Box.createVerticalStrut(15));
        
        JPanel productImportPanel = createSectionPanel("Thông tin sản phẩm & Phiếu nhập", createProductImportContent());
        productImportPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        container.add(productImportPanel);
        container.add(Box.createVerticalStrut(15));
        
        JPanel qtyPricePanel = createSectionPanel("Số lượng & Giá trị", createQtyPriceContent());
        qtyPricePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        container.add(qtyPricePanel);
        container.add(Box.createVerticalStrut(15));
        
        JPanel otherInfoPanel = createSectionPanel("Thông tin khác", createOtherInfoContent());
        otherInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        container.add(otherInfoPanel);
        
        container.add(Box.createVerticalGlue());
        
        mainPanel.add(container, BorderLayout.NORTH);
        
        return mainPanel;
    }

    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            new EmptyBorder(12, 18, 12, 18)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createLotInfoContent() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        txtLotId = createTextField();
        txtLotCode = createTextField();
        
        panel.add(createFieldPanel("Mã lô:", txtLotId));
        panel.add(createFieldPanel("Mã lô thực tế:", txtLotCode));
        
        return panel;
    }

    private JPanel createProductImportContent() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 12));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        txtProductName = createTextField();
        txtImportId = createTextField();
        txtProvider = createTextField();
        txtUser = createTextField();
        
        panel.add(createFieldPanel("Tên sản phẩm:", txtProductName));
        panel.add(createFieldPanel("Mã phiếu nhập:", txtImportId));
        panel.add(createFieldPanel("Nhà cung cấp:", txtProvider));
        panel.add(createFieldPanel("Người nhập:", txtUser));
        
        return panel;
    }

    private JPanel createQtyPriceContent() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        txtInitialQty = createTextField();
        txtCurrentQty = createTextField();
        txtImportPrice = createTextField();
        
        panel.add(createFieldPanel("Số lượng ban đầu:", txtInitialQty));
        panel.add(createFieldPanel("Số lượng hiện tại:", txtCurrentQty));
        panel.add(createFieldPanel("Giá nhập:", txtImportPrice));
        
        return panel;
    }

    private JPanel createOtherInfoContent() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        
        txtPrintYear = createTextField();
        txtStatus = createTextField();
        txtImportDate = createTextField();
        
        panel.add(createFieldPanel("Năm xuất bản:", txtPrintYear));
        panel.add(createFieldPanel("Trạng thái:", txtStatus));
        panel.add(createFieldPanel("Ngày nhập:", txtImportDate));
        
        return panel;
    }

    private JPanel createFieldPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(73, 80, 87));
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        
        textField.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(textField);
        
        return panel;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setEnabled(false);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBackground(new Color(248, 249, 250));
        textField.setDisabledTextColor(new Color(33, 37, 41));
        textField.setPreferredSize(new Dimension(0, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));
        
        btnHistory = new JButton("Lịch sử");
        btnHistory.setPreferredSize(new Dimension(110, 38));
        btnHistory.setBackground(new Color(13, 110, 253));
        btnHistory.setForeground(Color.WHITE);
        btnHistory.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHistory.setFocusPainted(false);
        btnHistory.setBorderPainted(false);
        btnHistory.addActionListener(this);
        
        btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(110, 38));
        btnClose.setBackground(new Color(108, 117, 125));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(this);
        
        panel.add(btnHistory);
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
        
        ImportDTO importDto = importBLL.getImportById(lot.getImportId());
        txtImportId.setText(String.valueOf(lot.getImportId()));
        
        if (importDto != null) {
            ProviderDTO provider = providerBLL.getProviderById(importDto.getProviderId());
            if (provider != null) {
                txtProvider.setText(provider.getProviderName());
            }
            
            String userName = userBLL.getUserNameById(importDto.getUserId());
            if (userName != null) {
                txtUser.setText(userName);
            }
        }
        
        txtInitialQty.setText(String.valueOf(lot.getInitialQuantity()));
        txtCurrentQty.setText(String.valueOf(lot.getQuantity()));
        txtImportPrice.setText(CURRENCY_FORMATTER.format(lot.getImportPrice()));
        
        txtPrintYear.setText(String.valueOf(lot.getPrintYear()));
        txtStatus.setText(lot.getStatus());
        txtImportDate.setText(lot.getImportDate().format(DATE_FORMATTER));
        
        if (lot.getQuantity() == 0) {
            txtCurrentQty.setDisabledTextColor(new Color(220, 38, 38));
        } else if (lot.getQuantity() < lot.getInitialQuantity() * 0.3) {
            txtCurrentQty.setDisabledTextColor(new Color(245, 158, 11));
        } else {
            txtCurrentQty.setDisabledTextColor(new Color(22, 163, 74));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnHistory) {
            new HistoryLotDialog((JFrame) this.getOwner(), lot);
        }
    }
}


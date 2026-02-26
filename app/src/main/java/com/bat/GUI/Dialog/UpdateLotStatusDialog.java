package com.bat.GUI.Dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.bat.BLL.LotBLL;
import com.bat.DTO.LotDTO;

public class UpdateLotStatusDialog extends JDialog implements ActionListener {
    private final LotDTO lot;
    private final LotBLL lotBLL = new LotBLL();
    
    private JTextField txtLotId, txtLotCode;
    private JComboBox<String> cboStatus;
    private JButton btnSave, btnCancel;
    
    private boolean isUpdated = false;

    public UpdateLotStatusDialog(JFrame parent, LotDTO lot) {
        super(parent, "Cập nhật trạng thái lô hàng", true);
        this.lot = lot;
        initComponents();
        loadData();
        this.setVisible(true);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());
        this.setSize(500, 350);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        
        // Main content
        JPanel mainPanel = createMainPanel();
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(mainPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setBackground(new Color(22, 122, 198));
        
        JLabel lblTitle = new JLabel("CẬP NHẬT TRẠNG THÁI LÔ HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        
        panel.add(lblTitle);
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0: Mã lô hàng
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        JLabel lblLotId = new JLabel("Mã lô:");
        lblLotId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblLotId, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtLotId = new JTextField();
        txtLotId.setPreferredSize(new Dimension(300, 35));
        txtLotId.setEditable(false);
        txtLotId.setBackground(new Color(240, 240, 240));
        txtLotId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtLotId, gbc);
        
        // Row 1: Mã code lô hàng
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel lblLotCode = new JLabel("Mã code:");
        lblLotCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblLotCode, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtLotCode = new JTextField();
        txtLotCode.setPreferredSize(new Dimension(300, 35));
        txtLotCode.setEditable(false);
        txtLotCode.setBackground(new Color(240, 240, 240));
        txtLotCode.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtLotCode, gbc);
        
        // Row 2: Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblStatus, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] statusOptions = {"Còn", "Hết", "Lỗi"};
        cboStatus = new JComboBox<>(statusOptions);
        cboStatus.setPreferredSize(new Dimension(300, 35));
        cboStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboStatus.setBackground(Color.WHITE);
        panel.add(cboStatus, gbc);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(22, 122, 198));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.addActionListener(this);
        
        btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(new Color(220, 53, 69));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.addActionListener(this);
        
        panel.add(btnSave);
        panel.add(btnCancel);
        
        return panel;
    }

    private void loadData() {
        if (lot != null) {
            txtLotId.setText(String.valueOf(lot.getLotId()));
            txtLotCode.setText(lot.getLotCode());
            
            // Set current status
            String currentStatus = lot.getStatus();
            if (currentStatus != null) {
                cboStatus.setSelectedItem(currentStatus);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            handleSave();
        } else if (e.getSource() == btnCancel) {
            this.dispose();
        }
    }

    private void handleSave() {
        String selectedStatus = (String) cboStatus.getSelectedItem();
        
        // Confirm before saving
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn cập nhật trạng thái lô hàng này?",
            "Xác nhận",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = lotBLL.updateLotStatus(lot.getLotId(), selectedStatus);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật trạng thái thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
                );
                lot.setStatus(selectedStatus); // Update the DTO
                isUpdated = true;
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật trạng thái thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public boolean isUpdated() {
        return isUpdated;
    }
}

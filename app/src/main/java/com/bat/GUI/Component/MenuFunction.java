package com.bat.GUI.Component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

public class MenuFunction extends JToolBar{
    public HashMap<String, JButton> buttons = new HashMap<>();
    
    public MenuFunction(String [] listBtn) {
        initData();
        initComponents(listBtn);
    }

    public void initData() {       
        buttons.put("create", new ButtonToolbar("Thêm", "add.svg", "create"));
        buttons.put("delete", new ButtonToolbar("Xóa", "delete.svg", "delete"));
        buttons.put("update", new ButtonToolbar("Sửa", "edit.svg", "update"));
        buttons.put("detail", new ButtonToolbar("Chi Tiết", "info.svg", "view"));
        buttons.put("export", new ButtonToolbar("Xuất Excel", "export_excel.svg", "view"));
    }

    private void initComponents(String[] listBtn) {
        this.setBackground(Color.WHITE);
        this.setRollover(true);  
        initData();
        
        for (String btnKey : listBtn) {
            if (buttons.containsKey(btnKey)) {
                JButton btn = buttons.get(btnKey);
                // Thiết lập kích thước chuẩn cho ButtonToolbar
                // btn.setPreferredSize(new Dimension(90, 90));
                // btn.setMinimumSize(new Dimension(90, 90));
                this.add(btn);
            }
        }
    }
    
    // Static method tạo menubar hoàn chỉnh với config
    public static JPanel createCompleteMenuBar(MenuBarConfig config) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(228, 238, 255));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header panel với title và buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Title panel bên trái
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(config.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(config.subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        // Button panel bên phải
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        buttonPanel.setOpaque(false);
        
        // Tạo MenuFunction instance để truy cập buttons HashMap
        MenuFunction menuFunction = new MenuFunction(config.buttonKeys);
        for (String btnKey : config.buttonKeys) {
            if (menuFunction.buttons.containsKey(btnKey)) {
                JButton btn = menuFunction.buttons.get(btnKey);
                // btn.setPreferredSize(new Dimension(72, 60));
                buttonPanel.add(btn);
            }
        }
        
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel ở dưới nếu có config
        if (config.searchOptions != null) {
            IntegratedSearch searchPanel = new IntegratedSearch(config.searchOptions);
            if (config.searchPlaceholder != null) {
                searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", config.searchPlaceholder);
            }
            // searchPanel.putClientProperty("FlatLaf.style", "arc: 15;");

            mainPanel.add(searchPanel, BorderLayout.SOUTH);
        }
        
        return mainPanel;
    }
}

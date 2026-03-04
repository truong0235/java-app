package com.bat.GUI.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class IntegratedSearch extends JPanel {

    public JComboBox<String> cbxChoose;
    public JButton btnReset;
    public JTextField txtSearchForm;

    private void initComponent(String str[]) {

        this.setBackground(Color.WHITE);
        BoxLayout bx = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(bx);

        JPanel jpSearch = new JPanel(new BorderLayout(8,10));
        jpSearch.setBorder(new EmptyBorder(5,5,5,5));
        jpSearch.setBackground(Color.white);
        cbxChoose = new JComboBox<>(new DefaultComboBoxModel<>(str));        
        // cbxChoose.setModel(new DefaultComboBoxModel<>(str));
        cbxChoose.setPreferredSize(new Dimension(150, 40));
        cbxChoose.setFont(new java.awt.Font(FlatRobotoFont.FAMILY, 0, 13));
        cbxChoose.setFocusable(false);
        jpSearch.add(cbxChoose,BorderLayout.WEST);

        txtSearchForm = new JTextField();
        txtSearchForm.setFont(new Font(FlatRobotoFont.FAMILY, 0, 13));
        txtSearchForm.setPreferredSize(new Dimension(320, 40));
        txtSearchForm.setMinimumSize(new Dimension(200, 40));
        txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập nội dung tìm kiếm...");
        txtSearchForm.putClientProperty("JTextField.showClearButton", true);
        jpSearch.add(txtSearchForm);

        btnReset = new JButton("Làm mới");
        btnReset.setPreferredSize(new Dimension(160, 30));
        btnReset.setFont(new java.awt.Font(FlatRobotoFont.FAMILY, 0, 14));
        btnReset.setBorderPainted(false);
        java.net.URL url = getClass().getResource("/icon/reload.svg");
        if (url != null) {
            btnReset.setIcon(new FlatSVGIcon(url));
        } else {
            System.err.println("Icon not found: /icon/reload.svg");
        }
        // btnReset.setPreferredSize(new Dimension(125, 0));
        // btnReset.addActionListener(this::btnResetActionPerformed);
        jpSearch.add(btnReset,BorderLayout.EAST);
        this.add(jpSearch);
    }

    public IntegratedSearch(String str[]) {
        initComponent(str);
    }

    // private void btnResetActionPerformed(java.awt.event.ActionEvent e) {
    //     txtSearchForm.setText("");
    //     cbxChoose.setSelectedIndex(0);
    // }
}

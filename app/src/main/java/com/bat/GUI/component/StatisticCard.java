package com.bat.GUI.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class StatisticCard extends JPanel {
    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel iconLabel;
    
    public StatisticCard(String title, String value, String iconText) {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(20, 25, 20, 25)
        ));
        setPreferredSize(new Dimension(320, 110));
        setMinimumSize(new Dimension(320, 110));
        setMaximumSize(new Dimension(400, 110));
        
        // Icon panel bên trái
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setOpaque(false);
        iconPanel.setPreferredSize(new Dimension(60, 70));
        
        iconLabel = new JLabel(iconText, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 40));
        iconLabel.setForeground(new Color(100, 100, 100));
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        
        add(iconPanel, BorderLayout.WEST);
        
        // Panel text bên phải
        JPanel textPanel = new JPanel(new BorderLayout(0, 5));
        textPanel.setOpaque(false);
        
        // Value label (số liệu lớn)
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        valueLabel.setForeground(new Color(51, 51, 51));
        valueLabel.setVerticalAlignment(SwingConstants.CENTER);
        textPanel.add(valueLabel, BorderLayout.NORTH);
        
        // Title label (mô tả)
        titleLabel = new JLabel("<html><div style='width:180px;'>" + title + "</div></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(120, 120, 120));
        titleLabel.setVerticalAlignment(SwingConstants.TOP);
        textPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(textPanel, BorderLayout.CENTER);
    }
    
    public void setValue(String value) {
        valueLabel.setText(value);
    }
    
    public void setTitle(String title) {
        titleLabel.setText("<html><div style='width:180px;'>" + title + "</div></html>");
    }
    
    public void setIcon(String iconText) {
        iconLabel.setText(iconText);
    }
}

package com.bat.GUI.Component;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JButton;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;

public class ButtonToolbar extends JButton{
    String permisson;
    
    public ButtonToolbar(String text, String icon, String permisson) {
        this.permisson = permisson;
        this.setFont(new java.awt.Font(FlatRobotoFont.FAMILY, 1, 14));
        this.setForeground(new Color(1, 88, 155));
        java.net.URL url = getClass().getResource("/icon/" + icon);
        if (url != null) {
            this.setIcon(new FlatSVGIcon(url));
        } else {
            System.err.println("Icon not found: /icon/" + icon);
        }
        this.setText(text);
        this.setFocusable(false);
        this.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        this.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.putClientProperty("JButton.buttonType", "toolBarButton");
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.setPreferredSize(new Dimension(80, 80));
        this.setMinimumSize(new Dimension(80, 80));
        this.putClientProperty("FlatLaf.style", "arc: 8; borderColor: #e0e0e0");
        
    }
    
    public String getPermisson() {
        return this.permisson;
    }
    
}

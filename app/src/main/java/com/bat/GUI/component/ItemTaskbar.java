package com.bat.GUI.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class ItemTaskbar extends JPanel implements MouseListener {
    private static final String FONT_SMALL = "font: 100% $medium.font";
    private static final String FONT_MEDIUM = "font: 145% $medium.font";
    private static final String FONT_LARGE = "font: 200% $medium.font";
    private static final String FONT_XLARGE = "font: 300% $semibold.font";
    private static final String FONT_SEMIBOLD = "font: 120% $semibold.font";
    private static final String FONT_MEDIUM_SEMIBOLD = "font: 150% $medium.font";
    
    Color FontColor = new Color(96, 125, 139);
    Color ColorBlack = new Color(26, 26, 26);
    Color DefaultColor = new Color(255, 255, 255);
    JLabel lblIcon, pnlContent, pnlSoLuong, pnlContent1;
    JPanel right;
    JLabel img;
    public boolean isSelected; 

    public ItemTaskbar(String linkIcon, String content) {
        this.setLayout(new FlowLayout(1, 10, 7));
        this.setPreferredSize(new Dimension(225, 45));
        this.setBackground(DefaultColor);
        this.putClientProperty( FlatClientProperties.STYLE, "arc: 15" );
        this.addMouseListener(this);
        lblIcon = new JLabel();
        lblIcon.setBorder(new EmptyBorder(0, 10, 0, 0));
        lblIcon.setPreferredSize(new Dimension(45, 30));
        FlatSVGIcon svg = loadSvgIcon("/icon/" + linkIcon);
        if (svg != null) {
            lblIcon.setIcon(svg);
        }
        this.add(lblIcon);

        pnlContent = new JLabel(content);
        pnlContent.setPreferredSize(new Dimension(155, 30));
        pnlContent.putClientProperty("FlatLaf.style", FONT_MEDIUM);
        pnlContent.setForeground(ColorBlack);
        this.add(pnlContent);
    }

    public ItemTaskbar(String linkIcon, String content1, String content2) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); 
        
        this.setPreferredSize(new Dimension(250, 150)); 
        this.setBackground(DefaultColor);
        this.putClientProperty( FlatClientProperties.STYLE, "arc: 15" );
        this.addMouseListener(this);

        lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(110, 110)); 
        
        FlatSVGIcon svg2 = loadSvgIcon("/icon/" + linkIcon);
        if (svg2 != null) {
            lblIcon.setIcon(svg2.derive(80, 80)); 
        }

        this.add(lblIcon);

        pnlContent = new JLabel(content1);
        pnlContent.setPreferredSize(new Dimension(170, 30));
        pnlContent.putClientProperty("FlatLaf.style", FONT_LARGE);
        pnlContent.setForeground(FontColor);
        this.add(pnlContent);

    }


    public ItemTaskbar(String linkIcon, String content, String content2, int n) {
        this.setLayout(new BorderLayout(0, 0));
        this.setBackground(DefaultColor);
        this.addMouseListener(this);
        lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(100, 100));
        lblIcon.setBorder(new EmptyBorder(0, 20, 0, 0));

        FlatSVGIcon svg3 = loadSvgIcon("/icon/" + linkIcon);
        if (svg3 != null) {
            lblIcon.setIcon(svg3);
        }
        this.add(lblIcon, BorderLayout.WEST);

        JPanel center = new JPanel();
        center.setLayout(new FlowLayout(0, 10, 0));
        center.setBorder(new EmptyBorder(20, 0, 0, 0));
        center.setOpaque(false);
        this.add(center);

        pnlContent = new JLabel(content);
        pnlContent.setPreferredSize(new Dimension(250, 30));
        pnlContent.putClientProperty("FlatLaf.style", FONT_XLARGE);
        pnlContent.setForeground(FontColor);
        center.add(pnlContent);

        pnlContent1 = new JLabel(content2);
        pnlContent1.setPreferredSize(new Dimension(250, 30));
        pnlContent1.putClientProperty("FlatLaf.style", FONT_MEDIUM_SEMIBOLD);
        pnlContent1.setForeground(FontColor);
        center.add(pnlContent1);
    }

    private FlatSVGIcon loadSvgIcon(String resourcePath) {
        java.net.URL url = getClass().getResource(resourcePath);
        if (url == null) {
            System.err.println("Icon not found: " + resourcePath);
            return null;
        }
        try {
            return new FlatSVGIcon(url);
        } catch (Exception ex) {
            System.err.println("Failed to load SVG icon: " + resourcePath + " — " + ex);
            return null;
        }
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!isSelected) {
            setBackground(new Color(235, 237, 240));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!isSelected) {
            setBackground(new Color(255, 255, 255));
        }
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
    }
    
}
 

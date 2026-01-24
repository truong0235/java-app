package com.bat.GUI;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.bat.GUI.feature.Category;
import com.bat.GUI.feature.Product;


public class HomePage {
    private JFrame frame;

    private JPanel navigationPanel;
    private JPanel contentPanel;
    public static void main(String[] args) {
        new HomePage().init();
    }

    private JButton navigationButton() {
        JButton button = new JButton();
        button.setBackground(null);
        button.setBorderPainted(true);
        button.setForeground(new Color(200, 200, 200));

        button.setPreferredSize(new Dimension(200, 50));
        button.setMinimumSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setFocusPainted(false);
        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                button.setBackground(new Color(255, 255, 255));
                button.setForeground(new Color(0, 0,0));
            }

            @Override
            public void focusLost(FocusEvent e) {
                button.setForeground(new Color(255, 255, 255));
                button.setBackground(null);
            }
        });
        return button;
    }

    private void navigationPanel() {
        this.navigationPanel = new JPanel();
        this.navigationPanel.setBackground(new Color(30, 30, 30));
        this.navigationPanel.setLayout(new BoxLayout(this.navigationPanel, BoxLayout.Y_AXIS));
        this.navigationPanel.setPreferredSize(new Dimension(200, 0));
        JButton productButton = navigationButton();
        productButton.setText("Product");
        productButton.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new Product());
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        JButton categoryButton = navigationButton();
        categoryButton.setText("Category");
        categoryButton.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(new Category());
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        JButton navButton1 = navigationButton();
        navButton1.setText("Dashboard");

        navigationPanel.add(navButton1);
        this.navigationPanel.add(productButton);
        this.navigationPanel.add(categoryButton);

    }

   private void contentPanel() {
        this.contentPanel = new JPanel();
        this.contentPanel.setBackground(new Color(240, 240, 0));
        this.contentPanel.setLayout(new BorderLayout());
   }

    public void init(){
        SwingUtilities.invokeLater(() -> {
            this.frame = new JFrame();
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.frame.setLayout(new BorderLayout());

            navigationPanel();
            contentPanel();

            frame.add(this.navigationPanel, BorderLayout.WEST);

            frame.add(this.contentPanel, BorderLayout.CENTER);

            frame.setSize(1200, 720);

            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }
}
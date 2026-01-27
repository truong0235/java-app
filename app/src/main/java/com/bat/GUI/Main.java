package com.bat.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.bat.GUI.feature.Category;
import com.bat.GUI.feature.Home;
import com.bat.GUI.feature.Product;

public class Main {
    private JFrame frame;

    private JPanel rightPanel;
    private JPanel contentPanel;

    public static void main(String[] args) {
        new Main().init();
    }

    private void rightPanel() {
        this.rightPanel = new JPanel();
        this.rightPanel.setBackground(new Color(30, 30, 30));
        this.rightPanel.setLayout(new BorderLayout());

        this.rightPanel.add(userPanel(), BorderLayout.NORTH);
        this.rightPanel.add(navigationPanel(), BorderLayout.CENTER);

    }

    private JButton navigationButton(String buttonString, JPanel panel) {
        JButton button = new JButton();

        button.setBackground(null);
        button.setForeground(new Color(200, 200, 200));
        button.setFocusPainted(false);
        button.setBorder(null);
        button.setBorderPainted(false);
        ;

        button.setPreferredSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));

        button.setText(buttonString);
        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                button.setBackground(new Color(255, 255, 255));
                button.setForeground(new Color(0, 0, 0));
            }

            @Override
            public void focusLost(FocusEvent e) {
                button.setForeground(new Color(255, 255, 255));
                button.setBackground(null);
            }
        });
        button.addActionListener(e -> {
            contentPanel.removeAll();
            contentPanel.add(panel);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        return button;
    }

 private JPanel userPanel() {
        JPanel userPanel = new JPanel();
        userPanel.setBackground(new Color(30, 30, 30)); // Background color to make it visible
        userPanel.setLayout(null); // Use null layout for absolute positioning

        // Create user name label
        JLabel userName = new JLabel("User: John Doe");
        userName.setBounds(10, 10, 200, 20); // Set bounds (x, y, width, height)
        userName.setForeground(new Color(200, 200, 200));
        userPanel.add(userName);

        // Create user role label
        JLabel userRole = new JLabel("Role: Admin");
        userRole.setBounds(10, 30, 200, 20); // Set bounds (x, y, width, height)
        userRole.setForeground(new Color(200, 200, 200));
        userPanel.add(userRole);

        // Create logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(null);
        logoutButton.setForeground(new Color(200, 200, 200));
        logoutButton.setBounds(10, 50, 100, 30); // Set bounds (x, y, width, height)

        logoutButton.addActionListener(e -> System.out.println("Logout clicked"));
        userPanel.add(logoutButton); // Add logout button to user panel

        userPanel.setPreferredSize(new Dimension(200, 100)); // Set preferred size for the panel

        return userPanel;
    }

    private JPanel navigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(30, 30, 30));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        JButton productButton = navigationButton("Product", new Product());

        JButton categoryButton = navigationButton("Category", new Category());

        JButton navButton1 = navigationButton("Home", new Home());

        navPanel.add(navButton1);
        navPanel.add(productButton);
        navPanel.add(categoryButton);
        return navPanel;
    }

    private void contentPanel() {
        this.contentPanel = new JPanel();
        this.contentPanel.setLayout(new BorderLayout());
    }

    public void init() {
        SwingUtilities.invokeLater(() -> {
            this.frame = new JFrame();
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.frame.setLayout(new BorderLayout());

            rightPanel();
            contentPanel();

            frame.add(this.rightPanel, BorderLayout.WEST);

            frame.add(this.contentPanel, BorderLayout.CENTER);

            frame.setSize(1200, 720);

            frame.setLocationRelativeTo(null);

            frame.setVisible(true);
        });
    }
}
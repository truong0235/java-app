package com.bat.GUI;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;

import com.bat.GUI.component.BaseJFrame;

public class LoginJFrame extends BaseJFrame {

    public LoginJFrame() {
        super(600, 400, "Login");
        init();
    }

    private JPanel usernamepanel() {
        JPanel JPanel = new JPanel();
        JPanel.setLayout(new BoxLayout(JPanel, BoxLayout.PAGE_AXIS));
        JPanel.setSize(300, 50);
        JLabel usernameLabel = new JLabel("Username:");
        JPanel.add(usernameLabel);
        JTextField usernameTextField = new JTextField();
        JPanel.add(usernameTextField);

        return JPanel;
    }

    private JPanel passwordpanel() {
        JPanel JPanel = new JPanel();
        JPanel.setLayout(new BoxLayout(JPanel, BoxLayout.PAGE_AXIS));
        JPanel.setSize(300, 50);
        JLabel passwordLabel = new JLabel("Password:");
        JPanel.add(passwordLabel);
        JTextField passwordTextField = new JTextField();
        JPanel.add(passwordTextField);

        return JPanel;
    }
    public void init() {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(3, 1));

        JLabel banner = new JLabel();

        banner.setText("Login");
        // banner.setBounds(150, 150, 100, 10);
        
        loginPanel.add(banner);
        loginPanel.add(usernamepanel());
        loginPanel.add(passwordpanel());
        
        add(loginPanel);
        revalidate();
        repaint();
    }
    
    public static void main(String[] args) {
        new LoginJFrame();
    }
}
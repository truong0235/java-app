package GUI;

import GUI.BaseJFrame;
import BLL.UserBLL;
import DTO.UserDTO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginJFrame extends BaseJFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private UserBLL userBLL = new UserBLL();

    public LoginJFrame() {
        super(550, 850, "Đăng nhập hệ thống");
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(1, 2));
        
        Color colorLeft = new Color(13, 110, 253);  
        Color colorRight = new Color(255, 255, 255); 
        Color colorTextRight = new Color(55, 65, 81); 
        Color colorBorder = new Color(209, 213, 219); 

        // --- PANEL TRÁI (LOGO) ---
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(colorLeft);
        JLabel lblTitle = new JLabel("HỆ THỐNG");
        JLabel lblSubTitle = new JLabel("QUẢN LÝ KHO SÁCH");
        
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 45));
        lblTitle.setForeground(Color.WHITE);
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        lblSubTitle.setForeground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        pnlLeft.add(lblTitle, gbc);
        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 0, 0);
        pnlLeft.add(lblSubTitle, gbc);
        add(pnlLeft);

        // --- PANEL PHẢI (FORM) ---
        JPanel pnlRight = new JPanel(null);
        pnlRight.setBackground(colorRight);

        JLabel lblLoginText = new JLabel("ĐĂNG NHẬP");
        lblLoginText.setForeground(colorTextRight);
        lblLoginText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLoginText.setBounds(50, 60, 200, 40);
        pnlRight.add(lblLoginText);

        JLabel lblUser = new JLabel("Username");
        lblUser.setForeground(colorTextRight);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setBounds(50, 140, 200, 30);
        pnlRight.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(50, 170, 320, 40);
        txtUsername.setBackground(colorRight);
        txtUsername.setForeground(colorTextRight);
        txtUsername.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, colorBorder));
        txtUsername.setCaretColor(colorTextRight);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnlRight.add(txtUsername);

        JLabel lblPass = new JLabel("Password");
        lblPass.setForeground(colorTextRight);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setBounds(50, 230, 200, 30); 
        pnlRight.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 260, 320, 40); 
        txtPassword.setBackground(colorRight);
        txtPassword.setForeground(colorTextRight);
        txtPassword.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, colorBorder));
        txtPassword.setCaretColor(colorTextRight);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pnlRight.add(txtPassword);
        
        // ==========================================
        // 👁 TÍNH NĂNG ẨN / HIỆN MẬT KHẨU
        // ==========================================
        char defaultEchoChar = txtPassword.getEchoChar(); 
        JCheckBox chkShowPass = new JCheckBox("Hiện mật khẩu");
        chkShowPass.setBounds(50, 310, 150, 30);
        chkShowPass.setBackground(colorRight);
        chkShowPass.setForeground(colorTextRight);
        chkShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkShowPass.setFocusPainted(false);
        chkShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) {
                txtPassword.setEchoChar((char) 0); 
            } else {
                txtPassword.setEchoChar(defaultEchoChar); 
            }
        });
        pnlRight.add(chkShowPass);

        // ==========================================
        // ❓ TÍNH NĂNG QUÊN MẬT KHẨU
        // ==========================================
        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setBounds(250, 310, 120, 30);
        lblForgot.setForeground(colorLeft); 
        lblForgot.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblForgot.setHorizontalAlignment(SwingConstants.RIGHT);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hiệu ứng di chuột và bấm vào Quên mật khẩu
        lblForgot.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lblForgot.setText("<html><u>Quên mật khẩu?</u></html>"); 
            }
            public void mouseExited(MouseEvent e) {
                lblForgot.setText("Quên mật khẩu?"); 
            }
            public void mouseClicked(MouseEvent e) {
                // ĐÃ SỬA: Gọi hàm hiển thị form xác minh 3 lớp
                showForgotPasswordDialog();
            }
        });
        pnlRight.add(lblForgot);
        
        // Sự kiện phím Enter
        KeyAdapter enterKey = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) checkLogin();
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        JButton btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBounds(50, 360, 320, 45);
        btnLogin.setBackground(colorLeft);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(e -> checkLogin());
        pnlRight.add(btnLogin);

        add(pnlRight);
        revalidate();
        repaint();
    }

    private void checkLogin() {
        String u = txtUsername.getText();
        String p = new String(txtPassword.getPassword());
        
        UserDTO user = userBLL.login(u, p);
        if (user != null) {
            this.dispose(); 
            SwingUtilities.invokeLater(() -> {
                new Main(user).init(); 
            });
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
        }
    }

    // ==========================================
    // FORM POPUP QUÊN MẬT KHẨU (XÁC MINH 3 LỚP)
    // ==========================================
    private void showForgotPasswordDialog() {
        JDialog dialog = new JDialog(this, "Khôi phục mật khẩu", true);
        dialog.setSize(400, 420); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 1, 10, 10)); 
        dialog.getContentPane().setBackground(Color.WHITE);
        ((JPanel)dialog.getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 1. Ô nhập Username
        JPanel pnlUser = new JPanel(new BorderLayout());
        pnlUser.setBackground(Color.WHITE);
        JLabel lblU = new JLabel("Tên đăng nhập:");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlUser.add(lblU, BorderLayout.NORTH);
        JTextField txtU = new JTextField();
        pnlUser.add(txtU, BorderLayout.CENTER);

        // 2. Ô nhập Số điện thoại
        JPanel pnlPhone = new JPanel(new BorderLayout());
        pnlPhone.setBackground(Color.WHITE);
        JLabel lblP = new JLabel("Số điện thoại (đã đăng ký):");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlPhone.add(lblP, BorderLayout.NORTH);
        JTextField txtP = new JTextField();
        pnlPhone.add(txtP, BorderLayout.CENTER);

        // 3. Ô nhập Email
        JPanel pnlEmail = new JPanel(new BorderLayout());
        pnlEmail.setBackground(Color.WHITE);
        JLabel lblE = new JLabel("Email (đã đăng ký):");
        lblE.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlEmail.add(lblE, BorderLayout.NORTH);
        JTextField txtE = new JTextField();
        pnlEmail.add(txtE, BorderLayout.CENTER);

        // 4. Ô nhập Mật khẩu mới
        JPanel pnlNewPass = new JPanel(new BorderLayout());
        pnlNewPass.setBackground(Color.WHITE);
        JLabel lblNP = new JLabel("Mật khẩu mới:");
        lblNP.setFont(new Font("Segoe UI", Font.BOLD, 13));
        pnlNewPass.add(lblNP, BorderLayout.NORTH);
        JPasswordField txtNP = new JPasswordField();
        pnlNewPass.add(txtNP, BorderLayout.CENTER);

        // 5. Nút Xác nhận
        JPanel pnlBtn = new JPanel(new BorderLayout());
        pnlBtn.setBackground(Color.WHITE);
        pnlBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JButton btnConfirm = new JButton("XÁC NHẬN ĐỔI MẬT KHẨU");
        btnConfirm.setBackground(new Color(13, 110, 253));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Xử lý sự kiện bấm nút
        btnConfirm.addActionListener(e -> {
            String u = txtU.getText();
            String p = txtP.getText();
            String em = txtE.getText(); 
            String np = new String(txtNP.getPassword());
            
            String msg = userBLL.resetPassword(u, p, em, np);
            JOptionPane.showMessageDialog(dialog, msg);
            
            if (msg.contains("thành công")) {
                txtUsername.setText(u);
                txtPassword.setText("");
                txtPassword.requestFocus();
                dialog.dispose();
            }
        });
        pnlBtn.add(btnConfirm, BorderLayout.CENTER);

        dialog.add(pnlUser);
        dialog.add(pnlPhone);
        dialog.add(pnlEmail);    
        dialog.add(pnlNewPass);
        dialog.add(pnlBtn);
        
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
        } catch (Exception e) {}

        java.awt.EventQueue.invokeLater(() -> {
            new LoginJFrame();
        });
    }
}
package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import GUI.Main;
import GUI.UserManagement; // Nhớ import đúng đường dẫn của bạn

public class MenuTaskbar extends JPanel {
    private Main mainApp;
    private JPanel pnlMenu;
    private JButton currentBtn;

    private Color colorHover = new Color(239, 246, 255); 
    private Color colorSelected = new Color(219, 234, 254); 
    private Color colorDefault = Color.WHITE;

    public MenuTaskbar(Main main) {
        this.mainApp = main;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new MatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));

        // --- 1. PHẦN ĐẦU (THÔNG TIN TÀI KHOẢN) ---
        JPanel pnlProfile = new JPanel();
        pnlProfile.setLayout(new BoxLayout(pnlProfile, BoxLayout.Y_AXIS));
        pnlProfile.setBackground(Color.WHITE);
        pnlProfile.setBorder(new MatteBorder(0, 0, 1, 0, new Color(229, 231, 235))); 
        pnlProfile.setPreferredSize(new Dimension(250, 100));

        // Lấy tên và chức vụ thực tế của người đăng nhập hiển thị lên
        String fullName = "Khách";
        String roleName = "Chưa rõ";
        if (mainApp.getCurrentUser() != null) {
            fullName = mainApp.getCurrentUser().getFullName();
            int role = mainApp.getCurrentUser().getRoleId();
            if(role == 1) roleName = "Quản trị viên (Admin)";
            else if(role == 2) roleName = "Quản lý (Manager)";
            else if(role == 3) roleName = "Kế toán";
            else if(role == 4) roleName = "Nhân viên Kho";
        }

        JLabel lblName = new JLabel(fullName);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setBorder(new EmptyBorder(20, 0, 5, 0));

        JLabel lblRole = new JLabel(roleName);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRole.setForeground(Color.GRAY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlProfile.add(lblName);
        pnlProfile.add(lblRole);
        add(pnlProfile, BorderLayout.NORTH);

        // --- 2. PHẦN GIỮA (CÁC NÚT MENU) ---
        pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setBackground(Color.WHITE);
        pnlMenu.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnHome = createMenuButton("Trang chủ");
        JButton btnProduct = createMenuButton("Sản phẩm");
        JButton btnUser = createMenuButton("Quản lý người dùng");

        btnHome.addActionListener(e -> setActiveButton(btnHome));
        
        btnUser.addActionListener(e -> {
            setActiveButton(btnUser);
            mainApp.setPanel(new UserManagement(mainApp.getCurrentUser())); 
        });

        pnlMenu.add(btnHome);
        pnlMenu.add(btnProduct);

        // ==========================================
        // 🔐 CHẶN KẾ TOÁN VÀ KHO KHỎI TRANG QUẢN LÝ USER
        // ==========================================
        if (mainApp.getCurrentUser() != null) {
            int role = mainApp.getCurrentUser().getRoleId();
            // Nếu là Role 1 (Admin) hoặc 2 (Manager) thì mới add nút User vào Menu
            if (role == 1 || role == 2) {
                pnlMenu.add(btnUser);
            }
        }

        add(pnlMenu, BorderLayout.CENTER);

        // --- 3. PHẦN CUỐI (NÚT ĐĂNG XUẤT) ---
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);
        
        JButton btnLogout = createMenuButton("Đăng xuất");
        btnLogout.setForeground(new Color(220, 38, 38)); 
        btnLogout.addActionListener(e -> {
            mainApp.dispose();
            new GUI.LoginJFrame().setVisible(true);
        });

        pnlBottom.add(btnLogout, BorderLayout.CENTER);
        pnlBottom.setBorder(new EmptyBorder(0, 0, 20, 0)); 
        add(pnlBottom, BorderLayout.SOUTH);

        // Nếu có nút User thì mặc định chọn nó
        if (btnUser.getParent() != null) {
            setActiveButton(btnUser);
        } else {
            setActiveButton(btnHome); // Không thì chọn trang chủ
        }
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(250, 45)); 
        btn.setPreferredSize(new Dimension(250, 45));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(55, 65, 81)); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 40, 0, 0)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != currentBtn) btn.setBackground(colorHover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentBtn) btn.setBackground(colorDefault);
            }
        });
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (currentBtn != null) {
            currentBtn.setBackground(colorDefault); 
        }
        currentBtn = btn;
        currentBtn.setBackground(colorSelected); 
    }
}
package com.bat.GUI.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.bat.DTO.UserDTO; // ĐÃ THÊM IMPORT
import com.bat.GUI.LoginJFrame;
import com.bat.GUI.Main;
import com.bat.GUI.panel.Category;
import com.bat.GUI.panel.Customer;
import com.bat.GUI.panel.Export;
import com.bat.GUI.panel.Home;
import com.bat.GUI.panel.Import;
import com.bat.GUI.panel.InventoryCheck;
import com.bat.GUI.panel.Lot;
import com.bat.GUI.panel.Product;
import com.bat.GUI.panel.Provider;
import com.bat.GUI.panel.UserManagement; // ĐÃ THÊM IMPORT
import com.formdev.flatlaf.extras.FlatSVGIcon;


public class MenuTaskbar extends JPanel{
    JScrollPane scrollPane;
    public ItemTaskbar[] listitem;
    Main mainFrame;

    JPanel pnlCenter, pnlTop, pnlBottom, bar1, bar2, bar3, bar4;
    JLabel lblUsername, lblTenNhomQuyen;

    Color FontColor = new Color(96, 125, 139);
    Color DefaultColor = new Color(255, 255, 255);
    Color HowerFontColor = new Color(1, 87, 155);
    Color HowerBackgroundColor = new Color(187, 222, 251);

    private final String[][] menuItem = {
            { "Trang chủ", "home.svg", "trangchu" },
            { "Sản phẩm", "product.svg", "sanpham" },
            { "Danh mục", "category.svg", "danhmuc" },
            { "Khách hàng", "customer.svg", "khachhang" },
            { "Quản lý người dùng", "users.svg", "nguoidung" },
            { "Nhà cung cấp", "provider.svg", "nhacungcap" },
            { "Phiếu nhập", "import.svg", "nhaphang" },
            { "Lô hàng", "lot.svg", "lohang" },
            { "xuat", "lot.svg", "xuat" },
            { "Phiếu kiểm kê", "check.svg", "kiemke" },
            { "Đăng xuất", "log_out.svg", "dangxuat" },
    };

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MenuTaskbar(Main main) {
        this.mainFrame = main;
        initComponent();
    }

    public void initComponent() {
        listitem = new ItemTaskbar[menuItem.length];
        this.setOpaque(true);
        this.setBackground(DefaultColor);
        this.setLayout(new BorderLayout(0, 0));

        pnlTop = new JPanel();
        pnlTop.setPreferredSize(new Dimension(250, 80));
        pnlTop.setBackground(DefaultColor);
        pnlTop.setLayout(new BorderLayout(0, 0));
        this.add(pnlTop, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BorderLayout(0, 0));
        pnlTop.add(info, BorderLayout.CENTER);

        addUserInfo(info);

        bar1 = new JPanel();
        bar1.setBackground(new Color(204, 214, 219));
        bar1.setPreferredSize(new Dimension(1, 0));
        pnlTop.add(bar1, BorderLayout.EAST);

        bar2 = new JPanel();
        bar2.setBackground(new Color(204, 214, 219));
        bar2.setPreferredSize(new Dimension(0, 1));
        pnlTop.add(bar2, BorderLayout.SOUTH);

        pnlCenter = new JPanel();
        pnlCenter.setPreferredSize(new Dimension(230, 600));
        pnlCenter.setBackground(DefaultColor);
        pnlCenter.setLayout(new FlowLayout(0, 0, 5));

        bar3 = new JPanel();
        bar3.setBackground(new Color(204, 214, 219));
        bar3.setPreferredSize(new Dimension(1, 1));
        this.add(bar3, BorderLayout.EAST);

        scrollPane = new JScrollPane(pnlCenter, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new EmptyBorder(5, 10, 0, 10));
        this.add(scrollPane, BorderLayout.CENTER);

        pnlBottom = new JPanel();
        pnlBottom.setPreferredSize(new Dimension(250, 50));
        pnlBottom.setBackground(DefaultColor);
        pnlBottom.setLayout(new BorderLayout(0, 0));

        bar4 = new JPanel();
        bar4.setBackground(new Color(204, 214, 219));
        bar4.setPreferredSize(new Dimension(1, 1));
        pnlBottom.add(bar4, BorderLayout.EAST);

        this.add(pnlBottom, BorderLayout.SOUTH);

        for (int i = 0; i < menuItem.length; i++) {
            if (i + 1 == menuItem.length) {
                listitem[i] = new ItemTaskbar(menuItem[i][1], menuItem[i][0]);
                pnlBottom.add(listitem[i]);
            } else {
                listitem[i] = new ItemTaskbar(menuItem[i][1], menuItem[i][0]);
                pnlCenter.add(listitem[i]);
            }
        }
        
        listitem[0].setBackground(HowerBackgroundColor);
        listitem[0].setForeground(HowerFontColor);
        listitem[0].isSelected = true;

        for (int i = 0; i < menuItem.length; i++) {
            listitem[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent evt) {
                    pnlMenuTaskbarMousePress(evt);
                }
            });
        }
    }

    public void pnlMenuTaskbarMousePress(MouseEvent evt) {
        for (int i = 0; i < menuItem.length; i++) {
            if (evt.getSource() == listitem[i]) {
                listitem[i].isSelected = true;
                listitem[i].setBackground(HowerBackgroundColor);
                listitem[i].setForeground(HowerFontColor);

                handleMenuClick(menuItem[i][0]);
            } else {
                listitem[i].isSelected = false;
                listitem[i].setBackground(DefaultColor);
                listitem[i].setForeground(FontColor);
            }
        }
    }

    public void addUserInfo(JPanel info) {
        // ĐÃ SỬA: Lấy dữ liệu tài khoản thực tế từ Main
        UserDTO user = mainFrame.getCurrentUser();
        
        String USERNAME = (user != null && user.getFullName() != null) ? user.getFullName() : "Khách";
        String TENNHOMQUYEN = "Chưa phân quyền";
        
        if (user != null) {
            switch(user.getRoleId()) {
                case 1 -> TENNHOMQUYEN = "Quản trị viên";
                case 2 -> TENNHOMQUYEN = "Giám đốc";
                case 3 -> TENNHOMQUYEN = "Trưởng phòng kho";
                case 4 -> TENNHOMQUYEN = "Quản lý kho";
                case 5 -> TENNHOMQUYEN = "Thủ kho";
                case 6 -> TENNHOMQUYEN = "Nhân viên nhập hàng";
                case 7 -> TENNHOMQUYEN = "Nhân viên kiểm kê";
                case 8 -> TENNHOMQUYEN = "Trưởng phòng kinh doanh";
                case 9 -> TENNHOMQUYEN = "Nhân viên bán hàng";
                case 10 -> TENNHOMQUYEN = "Nhân viên CSKH";
                case 11 -> TENNHOMQUYEN = "Nhân viên Marketing";
                case 12 -> TENNHOMQUYEN = "Trưởng phòng kế toán";
                case 13 -> TENNHOMQUYEN = "Kế toán";
                case 14 -> TENNHOMQUYEN = "Thủ quỹ";
                case 15 -> TENNHOMQUYEN = "Nhân viên IT";
                default -> TENNHOMQUYEN = "Chưa phân quyền";
            }
        }

        JPanel pnlIcon = new JPanel(new FlowLayout());
        pnlIcon.setPreferredSize(new Dimension(60, 0));
        pnlIcon.setOpaque(false);
        info.add(pnlIcon, BorderLayout.WEST);
        JLabel lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(50, 70));
        
        // Đoạn code icon nam nữ của bạn tôi giữ nguyên comment
        // if (nhanVienDTO.getGioitinh() == 1) {
        //     lblIcon.setIcon(new FlatSVGIcon("./src/icon/man_50px.svg"));
        // } else {
        //     lblIcon.setIcon(new FlatSVGIcon("./src/icon/women_50px.svg"));
        // }
        
        java.net.URL url = getClass().getResource("/icon/account.svg");
        if (url != null) {
            lblIcon.setIcon(new FlatSVGIcon(url));
        } else {
            System.err.println("Icon not found: /icon/account.svg");
        }
        pnlIcon.add(lblIcon);

        JPanel pnlInfo = new JPanel();
        pnlInfo.setOpaque(false);
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setBorder(new EmptyBorder(15, 0, 0, 0));
        info.add(pnlInfo, BorderLayout.CENTER);

        lblUsername = new JLabel(USERNAME);
        lblUsername.putClientProperty("FlatLaf.style", "font: 150% $semibold.font");
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUsername.setBorder(new EmptyBorder(0, 0, 6, 0));
        pnlInfo.add(lblUsername);

        lblTenNhomQuyen = new JLabel(TENNHOMQUYEN);
        lblTenNhomQuyen.putClientProperty("FlatLaf.style", "font: 120% $light.font");
        lblTenNhomQuyen.setForeground(Color.GRAY);
        lblTenNhomQuyen.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlInfo.add(lblTenNhomQuyen);
    }

    private void handleMenuClick(String menuName) {
        switch (menuName) {
            case "Trang chủ" -> mainFrame.setPanel(new Home());
            case "Sản phẩm" -> mainFrame.setPanel(new Product(mainFrame));
            case "Danh mục" -> mainFrame.setPanel(new Category(mainFrame)); 
            case "Khách hàng" -> mainFrame.setPanel(new Customer(mainFrame));
            
            // ĐÃ SỬA: Chuyền biến currentUser sang form UserManagement
            case "Quản lý người dùng" -> mainFrame.setPanel(new UserManagement(mainFrame.getCurrentUser())); 
            
            case "Nhà cung cấp" -> mainFrame.setPanel(new Provider(mainFrame));
            case "Phiếu kiểm kê" -> mainFrame.setPanel(new InventoryCheck(mainFrame));
            case "Phiếu nhập" -> mainFrame.setPanel(new Import(mainFrame));
            case "Lô hàng" -> mainFrame.setPanel(new Lot(mainFrame));
            case "xuat" -> mainFrame.setPanel(new Export(mainFrame));
            
            // Bắt sự kiện thoát
            case "Đăng xuất" -> {
                mainFrame.dispose();
                new LoginJFrame().setVisible(true);
            }
            default -> JOptionPane.showMessageDialog(mainFrame, "Chức năng đang phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

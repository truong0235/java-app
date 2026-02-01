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

import com.bat.GUI.Main;
import com.bat.GUI.panel.Home;
import com.bat.GUI.panel.Import;
import com.bat.GUI.panel.InventoryCheck;
import com.bat.GUI.panel.Product;
import com.formdev.flatlaf.extras.FlatSVGIcon;


public class MenuTaskbar extends JPanel{
    JScrollPane scrollPane;
    public ItemTaskbar[] listitem;
    Main mainFrame;
    
    //tasbarMenu chia thành 3 phần chính là pnlCenter, pnlTop, pnlBottom
    JPanel pnlCenter, pnlTop, pnlBottom, bar1, bar2, bar3, bar4;
    JLabel lblUsername, lblTenNhomQuyen;

    
    Color FontColor = new Color(96, 125, 139);
    Color DefaultColor = new Color(255, 255, 255);
    Color HowerFontColor = new Color(1, 87, 155);
    Color HowerBackgroundColor = new Color(187, 222, 251);
    private final String[][] menuItem = {
        {"Trang chủ", "home.svg", "trangchu"},
        {"Sản phẩm", "product.svg", "sanpham"},
        {"Phiếu nhập", "import.svg", "nhaphang"},
        {"Phiếu kiểm kê", "check.svg", "kiemke"},
        {"Đăng xuất", "log_out.svg", "dangxuat"},
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

        // bar1, bar là các đường kẻ mỏng giữa taskbarMenu và MainContent
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

        // thêm các item vào menu taskbar
        for (int i = 0; i < menuItem.length; i++) {
            if (i + 1 == menuItem.length) {
                listitem[i] = new ItemTaskbar(menuItem[i][1], menuItem[i][0]);
                pnlBottom.add(listitem[i]);
            } else {
                listitem[i] = new ItemTaskbar(menuItem[i][1], menuItem[i][0]);
                pnlCenter.add(listitem[i]);
                // if (i != 0) {
                //     if (!checkRole(menuItem[i][2])) {
                //         listitem[i].setVisible(false);
                //     }
                // }
            }
        }
        // Thiết lập hiệu ứng chọn mặc định cho mục 0
        listitem[0].setBackground(HowerBackgroundColor);
        listitem[0].setForeground(HowerFontColor);
        listitem[0].isSelected = true;

        /**
         * Xử lý khi click 1 item: đánh dấu isSelected cho item được click và reset item khác.
         * Container chịu trách nhiệm gán hành động (chuyển panel) theo index.
         */
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
        String USERNAME = "Hihihihihihihi";
        String TENNHOMQUYEN = "Admin";

        JPanel pnlIcon = new JPanel(new FlowLayout());
        pnlIcon.setPreferredSize(new Dimension(60, 0));
        pnlIcon.setOpaque(false);
        info.add(pnlIcon, BorderLayout.WEST);
        JLabel lblIcon = new JLabel();
        lblIcon.setPreferredSize(new Dimension(50, 70));
        // lblIcon.setBorder(new EmptyBorder(0, 10, 0, 0));
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
        // Use vertical layout so role (Admin) appears on next line under the username
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
            case "Phiếu kiểm kê" -> mainFrame.setPanel(new InventoryCheck(mainFrame));
            case "Phiếu nhập" -> mainFrame.setPanel(new Import(mainFrame));
            default -> JOptionPane.showMessageDialog(mainFrame, "Chức năng đang phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    

    



}

package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import GUI.MenuTaskbar;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import DTO.UserDTO;

public class Main extends JFrame {
    Color MainColor = new Color(250, 250, 250);
    private JPanel MainContent;
    private MenuTaskbar menuTaskbar;
    private UserDTO currentUser;

    public Main(UserDTO user) {
        this.currentUser = user;
    }

    // 👉 Hàm này dùng để MenuTaskbar lấy thông tin phân quyền
    public UserDTO getCurrentUser() {
        return this.currentUser;
    }

    public void init() {
        this.setTitle("Hệ thống quản lý kho sách");  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(1400, 800));
        this.setLocationRelativeTo(null);

        menuTaskbar = new MenuTaskbar(this);
        menuTaskbar.setPreferredSize(new Dimension(250, 1400));
        this.add(menuTaskbar, BorderLayout.WEST);

        MainContent = new JPanel();
        MainContent.setBackground(MainColor);
        MainContent.setLayout(new BorderLayout(0, 0));
        this.add(MainContent, BorderLayout.CENTER);
        
        // Mặc định load trang UserManagement nếu người dùng có quyền
        if (currentUser != null && (currentUser.getRoleId() == 1 || currentUser.getRoleId() == 2)) {
            setPanel(new UserManagement(currentUser));
        }
        
        this.setVisible(true);
    }

    public void setPanel(JPanel pn) {
        MainContent.removeAll();
        MainContent.add(pn, BorderLayout.CENTER);
        MainContent.revalidate();
        MainContent.repaint();
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.setPreferredFontFamily(FlatRobotoFont.FAMILY);
        try { UIManager.setLookAndFeel(new FlatIntelliJLaf()); } catch (Exception e) {}
        FlatIntelliJLaf.setup();
        
        SwingUtilities.invokeLater(() -> {
            new Main(new UserDTO()).init();
        });
    }
}
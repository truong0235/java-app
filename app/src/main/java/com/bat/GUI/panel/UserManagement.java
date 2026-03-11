package com.bat.GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bat.BLL.UserBLL;
import com.bat.DTO.UserDTO;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;
import com.formdev.flatlaf.extras.FlatSVGIcon;

public class UserManagement extends JPanel implements ActionListener {
    private UserBLL userBLL = new UserBLL();
    private DefaultTableModel tableModel;
    private JTable table;

    private IntegratedSearch searchPanel;
    private MenuFunction menuFunction;
    private JComboBox<String> cbbStatusFilter;
    private UserDTO currentUser;

    private final String[] ROLES_ARRAY = {
        "Quản trị viên", "Giám đốc", "Trưởng phòng kho", "Quản lý kho", 
        "Thủ kho", "Nhân viên nhập hàng", "Nhân viên kiểm kê", 
        "Trưởng phòng kinh doanh", "Nhân viên bán hàng", "Nhân viên CSKH", 
        "Nhân viên Marketing", "Trưởng phòng kế toán", "Kế toán", 
        "Thủ quỹ", "Nhân viên IT"
    };

    public UserManagement(UserDTO user) {
        this.currentUser = user;
        initComponent();
        performSearch(); 
    }

    public void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255)); 
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setBackground(new Color(228, 238, 255));
        menuBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý người dùng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Thông tin cơ bản người dùng và phân quyền");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        String[] navButtons = {"create", "update", "delete", "detail", "export"};
        menuFunction = new MenuFunction(navButtons);

        if (currentUser != null && currentUser.getRoleId() > 1) {
            if (menuFunction.buttons.containsKey("create")) menuFunction.buttons.get("create").setVisible(false);
            if (menuFunction.buttons.containsKey("update")) menuFunction.buttons.get("update").setVisible(false);
            if (menuFunction.buttons.containsKey("delete")) menuFunction.buttons.get("delete").setVisible(false);
        }

        for (String btnKey : navButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            if (btn != null) {
                btn.setActionCommand(btnKey);
                btn.addActionListener(this);
            }
        }

        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);

        String[] searchOptions = {"Tất cả", "Họ và tên", "Tên đăng nhập", "Số điện thoại"};
        searchPanel = new IntegratedSearch(searchOptions);
        searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập từ khóa tìm kiếm...");
        searchPanel.btnReset.setActionCommand("reset");
        searchPanel.btnReset.addActionListener(this);

        searchPanel.txtSearchForm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { performSearch(); }
        });
        searchPanel.cbxChoose.addActionListener(e -> performSearch());

        menuBar.add(searchPanel, BorderLayout.SOUTH);
        this.add(menuBar, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        this.add(tablePanel, BorderLayout.CENTER);

        JPanel filterPanel = createFilterPanel();
        this.add(filterPanel, BorderLayout.WEST);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 5, 5));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 250, 10));

        JPanel statusPn = new JPanel(new GridLayout(2, 1));
        statusPn.setBackground(Color.WHITE);
        JLabel lblStatus = new JLabel("Lọc theo trạng thái:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cbbStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Bị khoá"});
        cbbStatusFilter.addActionListener(e -> performSearch());

        statusPn.add(lblStatus);
        statusPn.add(cbbStatusFilter);

        panel.add(statusPn);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));

        String[] columns = {"ID", "Họ và tên", "Tên tài khoản", "Email", "Số điện thoại", "Nhóm quyền", "Trạng thái"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel col = table.getColumnModel();
        col.getColumn(0).setPreferredWidth(40); col.getColumn(0).setCellRenderer(centerRenderer); 
        col.getColumn(1).setPreferredWidth(160); 
        col.getColumn(2).setPreferredWidth(120); 
        col.getColumn(3).setPreferredWidth(180); 
        col.getColumn(4).setPreferredWidth(120); 
        col.getColumn(5).setPreferredWidth(120); 
        col.getColumn(6).setPreferredWidth(120); col.getColumn(6).setCellRenderer(new StatusCellRenderer()); 

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "create": showUserDialog(null, false); break;
            case "update": editUser(); break;
            case "delete": deleteUser(); break;
            case "detail": viewUserDetail(); break;
            case "export": exportExcel(); break;
            case "reset": resetForm(); break;
        }
    }

    private void performSearch() {
        String keyword = searchPanel.txtSearchForm.getText().toLowerCase().trim();
        String type = (String) searchPanel.cbxChoose.getSelectedItem();
        int statusFilterIndex = cbbStatusFilter.getSelectedIndex(); 

        tableModel.setRowCount(0);
        ArrayList<UserDTO> list = userBLL.getUserList();

        for (UserDTO u : list) {
            boolean matchKey = false;
            String name = (u.getFullName() != null) ? u.getFullName().toLowerCase() : "";
            String username = (u.getUsername() != null) ? u.getUsername().toLowerCase() : "";
            String phone = (u.getPhone() != null) ? u.getPhone().toLowerCase() : "";

            switch (type) {
                case "Họ và tên": if (name.contains(keyword)) matchKey = true; break;
                case "Tên đăng nhập": if (username.contains(keyword)) matchKey = true; break;
                case "Số điện thoại": if (phone.contains(keyword)) matchKey = true; break;
                default:
                    if (name.contains(keyword) || username.contains(keyword) || phone.contains(keyword)) matchKey = true;
                    break;
            }

            if (!matchKey) continue;

            String statusName = u.getStatus() == 1 ? "Hoạt động" : "Bị khoá";
            if (statusFilterIndex == 1 && u.getStatus() != 1) continue;
            if (statusFilterIndex == 2 && u.getStatus() != 0) continue;

            String roleName = (u.getRoleId() > 0 && u.getRoleId() <= ROLES_ARRAY.length) ? ROLES_ARRAY[u.getRoleId() - 1] : "Khác";

            tableModel.addRow(new Object[]{
                u.getUserId(), u.getFullName(), u.getUsername(), u.getEmail(), u.getPhone(), roleName, statusName
            });
        }
    }

    private void resetForm() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        cbbStatusFilter.setSelectedIndex(0);
        performSearch();
    }

    private void showUserDialog(UserDTO user, boolean isViewOnly) {
        boolean isEdit = (user != null);
        String title = isViewOnly ? "Chi tiết tài khoản" : (isEdit ? "Cập nhật tài khoản" : "Thêm tài khoản mới");
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(500, 800); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(new Color(13, 110, 253));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle);
        dialog.add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlBody = new JPanel(new BorderLayout());
        pnlBody.setBackground(Color.WHITE);

        JPanel pnlAvatar = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        pnlAvatar.setBackground(Color.WHITE);
        
        JLabel lblAvatarPic = new JLabel();
        lblAvatarPic.setPreferredSize(new Dimension(120, 120));
        lblAvatarPic.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatarPic.setBorder(BorderFactory.createLineBorder(new Color(209, 213, 219), 2));
        
        String avatarFile = (isEdit && user.getAvatar() != null && !user.getAvatar().isEmpty()) ? user.getAvatar() : "";
        
        boolean avatarLoaded = false;
        if (!avatarFile.isEmpty()) {
            try {
                File imageFile = new File("app/src/main/resources/image/" + avatarFile);
                if (imageFile.exists() && imageFile.isFile()) {
                    java.awt.Image img = new javax.swing.ImageIcon(imageFile.getAbsolutePath())
                        .getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                    lblAvatarPic.setIcon(new javax.swing.ImageIcon(img));
                    avatarLoaded = true;
                } else {
                    java.net.URL imgUrl = getClass().getResource("/image/" + avatarFile);
                    if (imgUrl != null) {
                        java.awt.Image img = new javax.swing.ImageIcon(imgUrl)
                            .getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                        lblAvatarPic.setIcon(new javax.swing.ImageIcon(img));
                        avatarLoaded = true;
                    }
                }
            } catch (Exception ex) {
            }
        }
        
        if (!avatarLoaded) {
            try {
                FlatSVGIcon defaultIcon = new FlatSVGIcon("icon/account.svg");
                lblAvatarPic.setIcon(defaultIcon.derive(100, 100));
            } catch (Exception ex) {
            }
        }

        pnlAvatar.add(lblAvatarPic);
        pnlBody.add(pnlAvatar, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(10, 2, 10, 10)); 
        pnlCenter.setBorder(new EmptyBorder(0, 40, 10, 40));
        pnlCenter.setBackground(Color.WHITE);

        JTextField txtId = new JTextField(isEdit ? String.valueOf(user.getUserId()) : "Tự động");
        txtId.setEnabled(false);

        JTextField txtName = new JTextField(isEdit ? user.getFullName() : "");
        JTextField txtUser = new JTextField(isEdit ? user.getUsername() : "");
        JPasswordField txtPass = new JPasswordField(isEdit ? user.getPassword() : "");
        JTextField txtEmail = new JTextField(isEdit ? user.getEmail() : "");
        JTextField txtPhone = new JTextField(isEdit ? user.getPhone() : "");
        JTextField txtAddress = new JTextField(isEdit ? user.getAddress() : "");
        JTextField txtAvatar = new JTextField(isEdit ? user.getAvatar() : "");
        txtAvatar.setEditable(false);
        
        JComboBox<String> cbRole = new JComboBox<>(ROLES_ARRAY);
        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Bị khoá", "Hoạt động"});

        if (isEdit) {
            txtUser.setEnabled(false); 
            if (user.getRoleId() > 0 && user.getRoleId() <= ROLES_ARRAY.length) {
                cbRole.setSelectedIndex(user.getRoleId() - 1);
            }
            cbStatus.setSelectedIndex(user.getStatus() == 1 ? 1 : 0);
        } else {
            cbStatus.setSelectedIndex(1);
        }

        if (isViewOnly) {
            txtName.setEditable(false); txtPass.setEditable(false);
            txtEmail.setEditable(false); txtPhone.setEditable(false);
            txtAddress.setEditable(false); txtAvatar.setEditable(false);
            cbRole.setEnabled(false); cbStatus.setEnabled(false);
        }

        JPanel pnlAvatarUpload = new JPanel(new BorderLayout(5, 0));
        pnlAvatarUpload.setBackground(Color.WHITE);
        pnlAvatarUpload.add(txtAvatar, BorderLayout.CENTER);
        
        JButton btnChooseImage = new JButton("Chọn ảnh");
        btnChooseImage.setBackground(new Color(13, 110, 253));
        btnChooseImage.setForeground(Color.WHITE);
        btnChooseImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChooseImage.setFocusPainted(false);
        btnChooseImage.setPreferredSize(new Dimension(100, 30));
        
        if (!isViewOnly) {
            btnChooseImage.addActionListener(evt -> {
                String username = txtUser.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Vui lòng nhập Username trước khi chọn ảnh!", 
                        "Cảnh báo", 
                        JOptionPane.WARNING_MESSAGE);
                    txtUser.requestFocus();
                    return;
                }
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn ảnh đại diện");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files (*.png, *.jpg, *.jpeg, *.svg)", "png", "jpg", "jpeg", "svg");
                fileChooser.setFileFilter(filter);
                
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String originalFileName = selectedFile.getName();
                    
                    String extension = "";
                    int lastDot = originalFileName.lastIndexOf('.');
                    if (lastDot > 0) {
                        extension = originalFileName.substring(lastDot);
                    }
                    
                    String newFileName = username + extension;
                    
                    try {
                        String resourcePath = "src/main/resources/image/";
                        File destDir = new File(resourcePath);
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        
                        File destFile = new File(destDir, newFileName);
                        
                        java.nio.file.Files.copy(
                            selectedFile.toPath(), 
                            destFile.toPath(), 
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );
                        
                        txtAvatar.setText(newFileName);
                        
                        try {
                            java.awt.Image img = new javax.swing.ImageIcon(destFile.getAbsolutePath())
                                .getImage().getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                            lblAvatarPic.setIcon(new javax.swing.ImageIcon(img));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(dialog, "Lỗi hiển thị ảnh preview: " + ex.getMessage());
                        }
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Upload ảnh thành công!\n", 
                            "Thành công", 
                            JOptionPane.INFORMATION_MESSAGE);
                            
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Lỗi khi copy file: " + ex.getMessage(), 
                            "Lỗi", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            pnlAvatarUpload.add(btnChooseImage, BorderLayout.EAST);
        }

        pnlCenter.add(new JLabel("Mã Nhân viên (ID):")); pnlCenter.add(txtId);
        pnlCenter.add(new JLabel("Họ và tên:")); pnlCenter.add(txtName);
        pnlCenter.add(new JLabel("Username:")); pnlCenter.add(txtUser);
        pnlCenter.add(new JLabel("Mật khẩu:")); pnlCenter.add(txtPass);
        pnlCenter.add(new JLabel("Email:")); pnlCenter.add(txtEmail);
        pnlCenter.add(new JLabel("Số điện thoại:")); pnlCenter.add(txtPhone);
        pnlCenter.add(new JLabel("Địa chỉ:")); pnlCenter.add(txtAddress);
        pnlCenter.add(new JLabel("Avatar:")); pnlCenter.add(pnlAvatarUpload);
        pnlCenter.add(new JLabel("Nhóm quyền:")); pnlCenter.add(cbRole);
        pnlCenter.add(new JLabel("Trạng thái:")); pnlCenter.add(cbStatus);
        
        pnlBody.add(pnlCenter, BorderLayout.CENTER);
        dialog.add(pnlBody, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel();
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(new EmptyBorder(0, 0, 15, 0));

        if (!isViewOnly) {
            JButton btnSave = new JButton("Lưu thông tin");
            btnSave.setBackground(new Color(13, 110, 253));
            btnSave.setForeground(Color.WHITE);
            btnSave.setPreferredSize(new Dimension(130, 35));
            btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnSave.addActionListener(e -> {
                String name = txtName.getText().trim();
                String uname = txtUser.getText().trim();
                String pass = new String(txtPass.getPassword()).trim();
                String email = txtEmail.getText().trim();
                String phone = txtPhone.getText().trim();
                String address = txtAddress.getText().trim();
                String avatar = txtAvatar.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Họ và tên không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    txtName.requestFocus();
                    return;
                }

                if (!isEdit && userBLL.isUsernameExists(uname)) {
                    JOptionPane.showMessageDialog(dialog, "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    txtUser.requestFocus();
                    return;
                }

                if (uname.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    txtUser.requestFocus();
                    return;
                }

                if (pass.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    txtPass.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Email không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    txtEmail.requestFocus();
                    return;
                }
                
                String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                if (!email.matches(emailRegex)) {
                    JOptionPane.showMessageDialog(dialog, "Email không hợp lệ!\nVui lòng nhập đúng định dạng (VD: abc@gmail.com)", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    txtEmail.requestFocus();
                    return;
                }

                if (phone.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Số điện thoại không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    txtPhone.requestFocus();
                    return;
                }
             
                String phoneRegex = "^(0[3|5|7|8|9]\\d{8}|02\\d{8})$";
                if (!phone.matches(phoneRegex)) {
                    JOptionPane.showMessageDialog(dialog, "Số điện thoại không hợp lệ!\nVui lòng nhập đúng 10 chữ số và bắt đầu bằng số 0.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    txtPhone.requestFocus();
                    return;
                }
                
                int id = isEdit ? user.getUserId() : -1;
                if (userBLL.isPhoneExists(phone, id)) {
                    JOptionPane.showMessageDialog(dialog, "Số điện thoại đã tồn tại! Vui lòng nhập số khác.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    txtPhone.requestFocus();
                    return;
                }
                // Cập nhật thông tin vào đối tượng UserDTO
                UserDTO u = new UserDTO();
                if (isEdit) u.setUserId(user.getUserId());
                u.setFullName(name);
                u.setUsername(uname);
                u.setPassword(pass);
                u.setEmail(email);
                u.setPhone(phone);
                u.setAddress(address);
                u.setAvatar(avatar);
                u.setRoleId(cbRole.getSelectedIndex() + 1);
                u.setStatus(cbStatus.getSelectedIndex());

                String msg = isEdit ? userBLL.updateUser(u) : userBLL.addUser(u);
                JOptionPane.showMessageDialog(dialog, msg);

                if (msg.contains("thành công")) {
                    performSearch();
                    dialog.dispose();
                }
            });
            pnlBottom.add(btnSave);
        } else {
            JButton btnClose = new JButton("Đóng");
            btnClose.setPreferredSize(new Dimension(100, 35));
            btnClose.addActionListener(e -> dialog.dispose());
            pnlBottom.add(btnClose);
        }
        dialog.add(pnlBottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editUser() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) table.getValueAt(row, 0);
            for (UserDTO u : userBLL.getUserList()) {
                if (u.getUserId() == id) {
                    showUserDialog(u, false);
                    break;
                }
            }
        } else JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trên bảng để sửa!");
    }

    private void viewUserDetail() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) table.getValueAt(row, 0);
            for (UserDTO u : userBLL.getUserList()) {
                if (u.getUserId() == id) {
                    showUserDialog(u, true);
                    break;
                }
            }
        } else JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản để xem chi tiết!");
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) table.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
                userBLL.deleteUser(id);
                performSearch();

                if (currentUser != null && currentUser.getUserId() == id) {
                    JOptionPane.showMessageDialog(this, "Bạn đã xóa tài khoản đang sử dụng. Hệ thống sẽ tự động đăng xuất!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    
                    JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                    if (mainFrame != null) {
                        mainFrame.dispose();
                    }
                    
                    java.awt.EventQueue.invokeLater(() -> {
                        new com.bat.GUI.LoginJFrame().setVisible(true);
                    });
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xóa!");
        }
    }

    private void exportExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".xlsx")) filePath += ".xlsx";

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Danh_Sach_Nhan_Vien");
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(table.getColumnName(i));
                }

                for (int i = 0; i < table.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object value = table.getValueAt(i, j);
                        if (value != null) row.createCell(j).setCellValue(value.toString());
                    }
                }
                for (int i = 0; i < table.getColumnCount(); i++) sheet.autoSizeColumn(i);

                FileOutputStream out = new FileOutputStream(filePath);
                workbook.write(out);
                out.close();
                workbook.close();

                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!\nĐã lưu tại: " + filePath);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                String status = value.toString();
                if (status.equals("Hoạt động")) setForeground(new Color(22, 163, 74));
                else if (status.equals("Bị khoá")) setForeground(new Color(220, 38, 38));
                setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return this;
        }
    }
}
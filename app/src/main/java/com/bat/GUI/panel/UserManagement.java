package com.bat.GUI.panel;
// package com.bat.GUI.Panel;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.bat.BLL.UserBLL;
import com.bat.DTO.UserDTO;
import java.awt.Color;
import java.awt.Font;
import com.bat.GUI.Component.MenuFunction;
import com.bat.GUI.Main;

public class UserManagement extends JPanel {
    private UserBLL userBLL = new UserBLL();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch; // Khai báo thêm biến ô tìm kiếm
    
    private UserDTO currentUser;

    public UserManagement(UserDTO user) {
        this.currentUser = user;
        
        setLayout(new BorderLayout());
        setBackground(new Color(243, 244, 246)); 

        // --- 1. HEADER PANEL ---
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1));
        pnlTitle.setBackground(Color.WHITE);
        JLabel lblTitle = new JLabel("Quản lý người dùng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pnlTitle.add(lblTitle);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setBackground(Color.WHITE);
        
        // 👉 TẠO THANH TÌM KIẾM
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(220, 35));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Sử dụng tính năng của FlatLaf để tạo chữ mờ gọi ý
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm tên, username, SĐT...");
        
        // Bắt sự kiện gõ phím để tìm kiếm realtime
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                // Lấy chữ đang gõ truyền vào hàm loadData để lọc
                loadData(txtSearch.getText().trim().toLowerCase());
            }
        });
        
        JButton btnAdd = createBtn("Thêm mới");
        JButton btnEdit = createBtn("Cập nhật");
        JButton btnDelete = createBtn("Xóa tài khoản");
        JButton btnRefresh = createBtn("Làm mới");
        JButton btnExcel = createBtn("Xuất Excel"); 
        
        // --- XỬ LÝ PHÂN QUYỀN ---
        if (currentUser != null) {
            int role = currentUser.getRoleId();
            if (role == 1) {
                // Admin giữ nguyên
            } else if (role == 2) {
                // Manager chỉ được xem, làm mới, xuất excel
                btnAdd.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
            } else {
                btnAdd.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
            }
        }
        
        btnAdd.addActionListener(e -> showUserDialog(null)); 
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());
        
        // Sửa lại nút Làm mới: Xóa nội dung tìm kiếm và tải lại toàn bộ
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadData("");
        });
        
        btnExcel.addActionListener(e -> exportExcel()); 
        
        // Thêm lần lượt vào Panel (Search sẽ đứng đầu tiên bên trái)
        pnlButtons.add(txtSearch);
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnEdit);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnExcel);
        pnlHeader.add(pnlButtons, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT PANEL ---
        JPanel pnlContent = new JPanel(new BorderLayout(0, 15));
        pnlContent.setBackground(new Color(243, 244, 246));
        pnlContent.setBorder(new EmptyBorder(15, 20, 20, 20));

        String[] cols = {"ID", "Họ và tên", "Tên đăng nhập", "Email", "Số điện thoại", "Nhóm quyền", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; } 
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35); 
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        pnlContent.add(scroll, BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);
        
        // Lần đầu mở lên thì truyền chuỗi rỗng để tải toàn bộ danh sách
        loadData("");
    }

    private JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(115, 35));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(13, 110, 253));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // 👉 ĐÃ SỬA: Hàm loadData giờ nhận thêm tham số "keyword" để lọc
    private void loadData(String keyword) {
        tableModel.setRowCount(0); 
        ArrayList<UserDTO> list = userBLL.getUserList();
        
        String[] roles = {"Quản trị viên", "Quản lý", "Nhân viên Sale", "Nhân viên Kho", "Kế toán"};
        
        for(UserDTO u : list) {
            // Gom tất cả thông tin lại thành 1 chuỗi dài để tìm kiếm cho dễ
            String searchStr = (u.getFullName() + " " + u.getUsername() + " " + 
                                u.getEmail() + " " + u.getPhone()).toLowerCase();
            
            // Nếu có từ khóa và chuỗi thông tin KHÔNG chứa từ khóa đó thì bỏ qua (không đưa lên bảng)
            if (keyword != null && !keyword.isEmpty() && !searchStr.contains(keyword)) {
                continue; 
            }
            
            String roleName = (u.getRoleId() > 0 && u.getRoleId() <= roles.length) ? roles[u.getRoleId()-1] : "Khác";
            tableModel.addRow(new Object[]{
                u.getUserId(), u.getFullName(), u.getUsername(), u.getEmail(), u.getPhone(),
                roleName,
                u.getStatus() == 1 ? "Hoạt động" : "Bị khóa"
            });
        }
    }

    private void showUserDialog(UserDTO user) {
        boolean isEdit = (user != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Cập nhật tài khoản" : "Thêm tài khoản mới", true);
        
        dialog.setSize(400, 480); 
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(7, 2, 10, 15)); 
        dialog.getContentPane().setBackground(Color.WHITE);
        ((JPanel)dialog.getContentPane()).setBorder(new EmptyBorder(15, 20, 15, 20));

        JTextField txtName = new JTextField(isEdit ? user.getFullName() : "");
        JTextField txtUser = new JTextField(isEdit ? user.getUsername() : "");
        JPasswordField txtPass = new JPasswordField(isEdit ? user.getPassword() : "");
        JTextField txtEmail = new JTextField(isEdit ? user.getEmail() : "");
        JTextField txtPhone = new JTextField(isEdit ? user.getPhone() : "");
        
        String[] roleArray = {"Quản trị viên", "Quản lý", "Nhân viên Sale", "Nhân viên Kho", "Kế toán"};
        JComboBox<String> cbRole = new JComboBox<>(roleArray);
        
        if (isEdit) {
            txtUser.setEnabled(false); 
            if(user.getRoleId() > 0 && user.getRoleId() <= roleArray.length) {
                cbRole.setSelectedIndex(user.getRoleId() - 1);
            }
        }

        dialog.add(new JLabel("Họ và tên:")); dialog.add(txtName);
        dialog.add(new JLabel("Username:")); dialog.add(txtUser);
        dialog.add(new JLabel("Mật khẩu:")); dialog.add(txtPass);
        dialog.add(new JLabel("Email:")); dialog.add(txtEmail);
        dialog.add(new JLabel("Số điện thoại:")); dialog.add(txtPhone); 
        dialog.add(new JLabel("Nhóm quyền:")); dialog.add(cbRole);

        JButton btnSave = new JButton("Lưu thông tin");
        btnSave.setBackground(new Color(74, 185, 126));
        btnSave.setForeground(Color.WHITE);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnSave.addActionListener(e -> {
            UserDTO u = new UserDTO();
            if (isEdit) u.setUserId(user.getUserId());
            u.setFullName(txtName.getText());
            u.setUsername(txtUser.getText());
            u.setPassword(new String(txtPass.getPassword()));
            u.setEmail(txtEmail.getText());
            u.setPhone(txtPhone.getText()); 
            u.setRoleId(cbRole.getSelectedIndex() + 1);
            u.setStatus(1);
            
            u.setAddress("");
            u.setAvatar("");

            String msg = isEdit ? userBLL.updateUser(u) : userBLL.addUser(u);
            JOptionPane.showMessageDialog(dialog, msg);
            
            if (msg.contains("thành công")) {
                // Cập nhật lại danh sách nhưng vẫn giữ nguyên trạng thái tìm kiếm hiện tại
                loadData(txtSearch.getText().trim().toLowerCase()); 
                dialog.dispose(); 
            }
        });

        dialog.add(new JLabel("")); dialog.add(btnSave);
        dialog.setVisible(true);
    }

    private void editUser() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) table.getValueAt(row, 0);
            for (UserDTO u : userBLL.getUserList()) {
                if (u.getUserId() == id) {
                    showUserDialog(u); 
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trên bảng để sửa!");
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            int id = (int)table.getValueAt(row, 0);
            if(JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                userBLL.deleteUser(id);
                // Giữ nguyên bộ lọc tìm kiếm hiện tại khi tải lại bảng
                loadData(txtSearch.getText().trim().toLowerCase());
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
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

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
                        if (value != null) {
                            row.createCell(j).setCellValue(value.toString());
                        }
                    }
                }

                for (int i = 0; i < table.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                FileOutputStream out = new FileOutputStream(filePath);
                workbook.write(out);
                out.close();
                workbook.close();

                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!\nĐã lưu tại: " + filePath);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel! Hãy kiểm tra xem file có đang được mở không.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}

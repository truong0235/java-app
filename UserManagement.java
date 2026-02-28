package GUI; // LƯU Ý: Nếu file của bạn nằm trong thư mục khác, hãy đổi tên package cho đúng nhé!

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

// Thư viện hỗ trợ xuất Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import BLL.UserBLL;
import DTO.UserDTO;
import java.awt.Color;
import java.awt.Font;

public class UserManagement extends JPanel {
    private UserBLL userBLL = new UserBLL();
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Biến lưu trữ người dùng hiện tại để Phân Quyền
    private UserDTO currentUser;

    // CONSTRUCTOR: Bắt buộc nhận UserDTO để biết ai đang đăng nhập
    public UserManagement(UserDTO user) {
        this.currentUser = user;
        
        setLayout(new BorderLayout());
        setBackground(new Color(243, 244, 246)); 

        // --- 1. HEADER PANEL (Tiêu đề và Nút bấm) ---
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
        
        JButton btnAdd = createBtn("Thêm mới");
        JButton btnEdit = createBtn("Cập nhật");
        JButton btnDelete = createBtn("Xóa tài khoản");
        JButton btnRefresh = createBtn("Làm mới");
        JButton btnExcel = createBtn("Xuất Excel"); 
        
        // ==========================================
        // 🔐 TÍNH NĂNG PHÂN QUYỀN (ROLE-BASED ACCESS)
        // ==========================================
        if (currentUser != null) {
            int role = currentUser.getRoleId();
            
            if (role == 1) {
                // ADMIN: Giữ nguyên tất cả các nút
            } 
            else if (role == 2) {
                // MANAGER: Ẩn nút Thêm, Sửa, Xóa (Chỉ được xem, làm mới và xuất Excel)
                btnAdd.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
            } 
            else {
                // CÁC NHÂN VIÊN KHÁC (Đề phòng lọt vào được trang này): Cũng ẩn luôn
                btnAdd.setVisible(false);
                btnEdit.setVisible(false);
                btnDelete.setVisible(false);
            }
        }
        // ==========================================
        
        // Gắn sự kiện cho các nút
        btnAdd.addActionListener(e -> showUserDialog(null)); // truyền null là Thêm mới
        btnEdit.addActionListener(e -> editUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnRefresh.addActionListener(e -> loadData());
        btnExcel.addActionListener(e -> exportExcel()); 
        
        // Thêm nút vào Panel
        pnlButtons.add(btnAdd);
        pnlButtons.add(btnEdit);
        pnlButtons.add(btnDelete);
        pnlButtons.add(btnRefresh);
        pnlButtons.add(btnExcel);
        pnlHeader.add(pnlButtons, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // --- 2. CONTENT PANEL (Bảng dữ liệu) ---
        JPanel pnlContent = new JPanel(new BorderLayout(0, 15));
        pnlContent.setBackground(new Color(243, 244, 246));
        pnlContent.setBorder(new EmptyBorder(15, 20, 20, 20));

        String[] cols = {"ID", "Họ và tên", "Tên đăng nhập", "Email", "Nhóm quyền", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; } // Không cho sửa trực tiếp trên bảng
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35); // Cho dòng cao lên nhìn thoáng hơn
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        pnlContent.add(scroll, BorderLayout.CENTER);

        add(pnlContent, BorderLayout.CENTER);
        
        // Tải dữ liệu lần đầu khi mở trang
        loadData();
    }

    // Hàm tạo nút bấm cho đẹp
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

    // Hàm tải dữ liệu từ Database lên Bảng
    private void loadData() {
        tableModel.setRowCount(0); // Xóa sạch dữ liệu cũ trên bảng
        ArrayList<UserDTO> list = userBLL.getUserList();
        String[] roles = {"Quản trị viên", "Quản lý", "Kế toán", "Kho"};
        
        for(UserDTO u : list) {
            String roleName = (u.getRoleId() > 0 && u.getRoleId() <= roles.length) ? roles[u.getRoleId()-1] : "Khác";
            tableModel.addRow(new Object[]{
                u.getUserId(), u.getFullName(), u.getUsername(), u.getEmail(), 
                roleName,
                u.getStatus() == 1 ? "Hoạt động" : "Bị khóa"
            });
        }
    }

    // --- DIALOG THÊM / CẬP NHẬT TÀI KHOẢN ---
    private void showUserDialog(UserDTO user) {
        boolean isEdit = (user != null);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), isEdit ? "Cập nhật tài khoản" : "Thêm tài khoản mới", true);
        dialog.setSize(400, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(6, 2, 10, 20));
        dialog.getContentPane().setBackground(Color.WHITE);
        ((JPanel)dialog.getContentPane()).setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField txtName = new JTextField(isEdit ? user.getFullName() : "");
        JTextField txtUser = new JTextField(isEdit ? user.getUsername() : "");
        JPasswordField txtPass = new JPasswordField(isEdit ? user.getPassword() : "");
        JTextField txtEmail = new JTextField(isEdit ? user.getEmail() : "");
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Quản trị viên", "Quản lý", "Kế toán", "Kho"});
        
        if (isEdit) {
            txtUser.setEnabled(false); // Đang sửa thì KHÔNG cho đổi Username
            cbRole.setSelectedIndex(user.getRoleId() - 1);
        }

        dialog.add(new JLabel("Họ và tên:")); dialog.add(txtName);
        dialog.add(new JLabel("Username:")); dialog.add(txtUser);
        dialog.add(new JLabel("Mật khẩu:")); dialog.add(txtPass);
        dialog.add(new JLabel("Email:")); dialog.add(txtEmail);
        dialog.add(new JLabel("Nhóm quyền:")); dialog.add(cbRole);

        JButton btnSave = new JButton("Lưu thông tin");
        btnSave.setBackground(new Color(74, 185, 126));
        btnSave.setForeground(Color.WHITE);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> {
            // Ráp dữ liệu từ form vào đối tượng UserDTO
            UserDTO u = new UserDTO();
            if (isEdit) u.setUserId(user.getUserId());
            u.setFullName(txtName.getText());
            u.setUsername(txtUser.getText());
            u.setPassword(new String(txtPass.getPassword()));
            u.setEmail(txtEmail.getText());
            u.setRoleId(cbRole.getSelectedIndex() + 1);
            u.setStatus(1);

            // Gọi BLL để xử lý
            String msg = isEdit ? userBLL.updateUser(u) : userBLL.addUser(u);
            JOptionPane.showMessageDialog(dialog, msg);
            loadData(); // Cập nhật lại bảng
            dialog.dispose(); // Tắt popup
        });

        dialog.add(new JLabel("")); dialog.add(btnSave);
        dialog.setVisible(true);
    }

    // --- HÀM GỌI CẬP NHẬT ---
    private void editUser() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) table.getValueAt(row, 0);
            for (UserDTO u : userBLL.getUserList()) {
                if (u.getUserId() == id) {
                    showUserDialog(u); // Mở popup và truyền thông tin user cũ vào
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tài khoản trên bảng để sửa!");
        }
    }

    // --- HÀM GỌI XÓA ---
    private void deleteUser() {
        int row = table.getSelectedRow();
        if(row >= 0) {
            int id = (int)table.getValueAt(row, 0);
            if(JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa tài khoản này?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                userBLL.deleteUser(id);
                loadData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để xóa!");
        }
    }

    // --- HÀM XUẤT EXCEL ---
    private void exportExcel() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file Excel");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();
                
                // Tự động thêm đuôi .xlsx nếu người dùng quên gõ
                if (!filePath.toLowerCase().endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("Danh_Sach_Nhan_Vien");

                // Tạo dòng Header
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < table.getColumnCount(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(table.getColumnName(i));
                }

                // Chép dữ liệu từ Table ra dòng
                for (int i = 0; i < table.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object value = table.getValueAt(i, j);
                        if (value != null) {
                            row.createCell(j).setCellValue(value.toString());
                        }
                    }
                }

                // Chỉnh độ rộng cột tự động
                for (int i = 0; i < table.getColumnCount(); i++) {
                    sheet.autoSizeColumn(i);
                }

                // Ghi file
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
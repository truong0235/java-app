package com.bat.GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;

public class Perm extends JPanel implements ActionListener {

    // ----------------------------------------------------------------
    // Data model
    // ----------------------------------------------------------------
    /** role_id → {name, status} */
    private static final Object[][] ROLES = {
        {1,  "Admin",                   1},
        {2,  "Quản lý kho",             1},
        {3,  "Nhân viên nhập hàng",     1},
        {4,  "Nhân viên bán hàng",      1},
        {5,  "Kế toán",                 1},
        {6,  "Thủ kho",                 1},
        {7,  "Giám đốc",                1},
        {8,  "Nhân viên kiểm kê",       1},
        {10, "Nhân viên CSKH",          1},
    };

    /**
     * Permissions per role_id.
     * Each entry: {perm_id, resource_name, perm_value}
     * perm_value bitmask: 4=read, 2=write, 1=execute
     */
    private static final Map<Integer, List<Object[]>> PERMS = new LinkedHashMap<>();
    static {
        // role 1: Admin – full access everywhere
        add(1,  1, "Danh mục (category)",        7);
        add(2,  1, "Sản phẩm (product)",          7);
        add(3,  1, "Nhà cung cấp (provider)",     7);
        add(4,  1, "Khách hàng (customer)",        7);
        add(5,  1, "Phiếu nhập (import)",          7);
        add(6,  1, "Phiếu xuất (export)",          7);
        add(7,  1, "Lô hàng (lot)",               7);
        add(8,  1, "Kiểm kê (inventory_check)",   7);
        add(9,  1, "Người dùng (user)",            7);
        add(10, 1, "Quyền hạn (permission)",       7);
        add(11, 1, "Thống kê (statistic)",         7);

        // role 2: Quản lý kho
        add(12, 2, "Danh mục (category)",          6); // rw-
        add(13, 2, "Sản phẩm (product)",            7); // rwx
        add(14, 2, "Nhà cung cấp (provider)",       6);
        add(15, 2, "Phiếu nhập (import)",            7);
        add(16, 2, "Phiếu xuất (export)",            6);
        add(17, 2, "Lô hàng (lot)",                 7);
        add(18, 2, "Kiểm kê (inventory_check)",     7);
        add(19, 2, "Thống kê (statistic)",           4); // r--

        // role 3: Nhân viên nhập hàng
        add(20, 3, "Sản phẩm (product)",             4);
        add(21, 3, "Nhà cung cấp (provider)",         4);
        add(22, 3, "Phiếu nhập (import)",              6);
        add(23, 3, "Lô hàng (lot)",                   4);

        // role 4: Nhân viên bán hàng
        add(24, 4, "Sản phẩm (product)",              4);
        add(25, 4, "Khách hàng (customer)",             6);
        add(26, 4, "Phiếu xuất (export)",               6);
        add(27, 4, "Lô hàng (lot)",                    4);

        // role 5: Kế toán
        add(28, 5, "Phiếu nhập (import)",              4);
        add(29, 5, "Phiếu xuất (export)",               4);
        add(30, 5, "Thống kê (statistic)",              5); // r-x
        add(31, 5, "Khách hàng (customer)",              4);

        // role 6: Thủ kho
        add(32, 6, "Sản phẩm (product)",               4);
        add(33, 6, "Lô hàng (lot)",                    6);
        add(34, 6, "Kiểm kê (inventory_check)",        7);

        // role 7: Giám đốc – read-only + export statistic
        add(35, 7, "Sản phẩm (product)",               4);
        add(36, 7, "Phiếu nhập (import)",               4);
        add(37, 7, "Phiếu xuất (export)",                4);
        add(38, 7, "Thống kê (statistic)",               5);

        // role 8: Nhân viên kiểm kê
        add(39, 8, "Sản phẩm (product)",               4);
        add(40, 8, "Lô hàng (lot)",                    4);
        add(41, 8, "Kiểm kê (inventory_check)",        6);

        // role 10: Nhân viên CSKH
        add(42, 10, "Khách hàng (customer)",            6);
        add(43, 10, "Phiếu xuất (export)",               4);
    }

    private static void add(int permId, int roleId, String resource, int permValue) {
        PERMS.computeIfAbsent(roleId, k -> new ArrayList<>())
             .add(new Object[]{permId, resource, permValue});
    }

    // ----------------------------------------------------------------
    // UI fields
    // ----------------------------------------------------------------
    private MenuFunction menuFunction;
    private IntegratedSearch searchPanel;
    private DefaultTableModel tableModel;
    private JTable table;

    public Perm() {
        initComponent();
        loadRoles(ROLES);
    }

    private void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setBackground(new Color(228, 238, 255));
        menuBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý quyền hạn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Chọn vai trò → Chi tiết để xem quyền theo chức năng");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Buttons
        String[] navButtons = {"create", "update", "delete", "detail"};
        menuFunction = new MenuFunction(navButtons);
        for (String btnKey : navButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            if (btn != null) {
                btn.setActionCommand(btnKey);
                btn.addActionListener(this);
            }
        }
        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);

        // Search
        String[] searchOptions = {"Tất cả", "Mã vai trò", "Tên vai trò"};
        searchPanel = new IntegratedSearch(searchOptions);
        searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập từ khóa...");
        searchPanel.btnReset.setActionCommand("reset");
        searchPanel.btnReset.addActionListener(this);
        searchPanel.txtSearchForm.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) { performSearch(); }
        });
        searchPanel.cbxChoose.addActionListener(e -> performSearch());

        menuBar.add(searchPanel, BorderLayout.SOUTH);
        this.add(menuBar, BorderLayout.NORTH);
        this.add(createTablePanel(), BorderLayout.CENTER);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));

        // Main table: ID | Tên vai trò | Trạng thái
        String[] columns = {"Mã vai trò", "Tên vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);

        table.setRowHeight(42);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(0, 42));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        TableColumnModel col = table.getColumnModel();
        col.getColumn(0).setPreferredWidth(100); col.getColumn(0).setCellRenderer(centerRenderer);
        col.getColumn(1).setPreferredWidth(400);
        col.getColumn(2).setPreferredWidth(120); col.getColumn(2).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadRoles(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(new Object[]{
                row[0],
                row[1],
                ((int) row[2] == 1) ? "Hoạt động" : "Không hoạt động"
            });
        }
    }

    private void performSearch() {
        String keyword = searchPanel.txtSearchForm.getText().trim().toLowerCase();
        String type    = (String) searchPanel.cbxChoose.getSelectedItem();
        tableModel.setRowCount(0);
        for (Object[] row : ROLES) {
            String id   = row[0].toString();
            String name = row[1].toString().toLowerCase();
            boolean match = switch (type) {
                case "Mã vai trò"  -> id.contains(keyword);
                case "Tên vai trò" -> name.contains(keyword);
                default            -> id.contains(keyword) || name.contains(keyword);
            };
            if (match) {
                tableModel.addRow(new Object[]{
                    row[0], row[1],
                    ((int) row[2] == 1) ? "Hoạt động" : "Không hoạt động"
                });
            }
        }
    }

    /** Convert bitmask to rwx string */
    private static String toRwx(int v) {
        return ((v & 4) != 0 ? "r" : "-")
             + ((v & 2) != 0 ? "w" : "-")
             + ((v & 1) != 0 ? "x" : "-");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "create" ->
                JOptionPane.showMessageDialog(this, "Chức năng Thêm vai trò (chưa triển khai)");

            case "update" -> {
                if (table.getSelectedRow() == -1)
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn vai trò cần sửa!");
                else
                    JOptionPane.showMessageDialog(this, "Chức năng Sửa vai trò (chưa triển khai)");
            }

            case "delete" -> {
                if (table.getSelectedRow() == -1)
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn vai trò cần xóa!");
                else
                    JOptionPane.showMessageDialog(this, "Chức năng Xóa vai trò (chưa triển khai)");
            }

            case "detail" -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn vai trò để xem chi tiết!");
                } else {
                    int    roleId   = (int)    tableModel.getValueAt(row, 0);
                    String roleName = (String) tableModel.getValueAt(row, 1);
                    showPermDetail(roleId, roleName);
                }
            }

            case "reset" -> {
                searchPanel.txtSearchForm.setText("");
                searchPanel.cbxChoose.setSelectedIndex(0);
                loadRoles(ROLES);
            }
        }
    }

    /** Opens a detail dialog showing all permissions for the selected role */
    private void showPermDetail(int roleId, String roleName) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Chi tiết quyền – " + roleName, true);
        dialog.setSize(600, 420);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(13, 110, 253));
        header.setPreferredSize(new Dimension(0, 50));
        JLabel lblTitle = new JLabel("Quyền hạn của: " + roleName);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        header.add(lblTitle);
        dialog.add(header, BorderLayout.NORTH);

        // Table: Mã quyền | Tên chức năng | Quyền (rwx) | Giá trị
        String[] cols = {"Mã quyền", "Tên chức năng", "Quyền (rwx)", "Giá trị"};
        DefaultTableModel model = new DefaultTableModel(null, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Object[]> perms = PERMS.getOrDefault(roleId, new ArrayList<>());
        for (Object[] p : perms) {
            int val = (int) p[2];
            model.addRow(new Object[]{p[0], p[1], toRwx(val), val});
        }
        if (perms.isEmpty()) {
            model.addRow(new Object[]{"—", "(Không có quyền nào)", "---", 0});
        }

        JTable detailTable = new JTable(model);
        detailTable.setRowHeight(36);
        detailTable.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detailTable.setShowVerticalLines(false);
        detailTable.setShowHorizontalLines(true);
        detailTable.setGridColor(new Color(229, 231, 235));
        detailTable.setSelectionBackground(new Color(239, 246, 255));
        detailTable.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader dHeader = detailTable.getTableHeader();
        dHeader.setBackground(new Color(248, 249, 250));
        dHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dHeader.setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        TableColumnModel dcm = detailTable.getColumnModel();
        dcm.getColumn(0).setPreferredWidth(70);  dcm.getColumn(0).setCellRenderer(center);
        dcm.getColumn(1).setPreferredWidth(280);
        dcm.getColumn(2).setPreferredWidth(90);  dcm.getColumn(2).setCellRenderer(center);
        dcm.getColumn(3).setPreferredWidth(70);  dcm.getColumn(3).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(detailTable);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        dialog.add(scroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(10, 0, 10, 0));
        JButton btnClose = new JButton("Đóng");
        btnClose.setBackground(new Color(220, 53, 69));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(ev -> dialog.dispose());
        footer.add(btnClose);
        dialog.add(footer, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}

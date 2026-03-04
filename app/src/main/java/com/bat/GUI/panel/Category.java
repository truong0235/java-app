package com.bat.GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bat.BLL.CategoryBLL;
import com.bat.DTO.CategoryDTO;
import com.bat.GUI.Main;
import com.bat.GUI.component.ButtonToolbar;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;

public class Category extends JPanel implements ActionListener {
    private Main main;
    private CategoryBLL categoryBLL = new CategoryBLL();
    private DefaultTableModel tableModel;
    private JTable table;

    private IntegratedSearch searchPanel;
    private MenuFunction menuFunction;
    private JComboBox<String> cbbStatusFilter;

    public Category(Main main) {
        this.main = main;
        initComponent();
        loadDataTable(categoryBLL.getCategoryList());
    }

    public void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setBackground(new Color(228, 238, 255));
        menuBar.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý danh mục");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Phân loại và cấu trúc kho sản phẩm");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Menu Function Buttons
        String[] navButtons = {"create", "update", "delete", "detail", "export"};
        menuFunction = new MenuFunction(navButtons);

        for (String btnKey : navButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            if (btn != null) {
                btn.setActionCommand(btnKey);
                btn.addActionListener(this);
            }
        }

        ButtonToolbar btnImport = new ButtonToolbar("Nhập Excel", "import_excel.svg", "create");
        btnImport.setActionCommand("import");
        btnImport.addActionListener(this);
        menuFunction.add(btnImport);

        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);

        // Search Panel
        String[] searchOptions = {"Tất cả", "Tên danh mục", "Mã loại"};
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

        cbbStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngừng hoạt động"});
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

        String[] columns = {"Mã loại", "Tên danh mục", "Mô tả", "Trạng thái"};
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
        col.getColumn(0).setPreferredWidth(80); col.getColumn(0).setCellRenderer(centerRenderer);
        col.getColumn(1).setPreferredWidth(250);
        col.getColumn(2).setPreferredWidth(400);
        col.getColumn(3).setPreferredWidth(120); col.getColumn(3).setCellRenderer(centerRenderer);

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
            case "create":
                new CategoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), "THÊM DANH MỤC", null).setVisible(true);
                refreshData();
                break;
            case "update":
                int rowEdit = table.getSelectedRow();
                if (rowEdit != -1) {
                    int id = Integer.parseInt(table.getValueAt(rowEdit, 0).toString());
                    CategoryDTO c = categoryBLL.getCategoryById(id);
                    if (c != null) {
                        new CategoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), "CẬP NHẬT DANH MỤC", c).setVisible(true);
                        refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục cần sửa!");
                }
                break;
            case "delete":
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để xóa!");
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa " + selectedRows.length + " danh mục?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        int success = 0;
                        for (int i : selectedRows) {
                            int id = Integer.parseInt(table.getValueAt(i, 0).toString());
                            String msg = categoryBLL.delete(id);
                            if (msg.contains("thành công")) success++;
                        }
                        JOptionPane.showMessageDialog(this, "Đã xóa thành công " + success + " mục!");
                        refreshData();
                    }
                }
                break;
            case "export":
                exportExcel();
                break;
            case "import":
                importExcel();
                break;
            case "reset":
                resetForm();
                break;
            case "detail":
                int rowDetail = table.getSelectedRow();
                if (rowDetail != -1) {
                    int id = Integer.parseInt(table.getValueAt(rowDetail, 0).toString());
                    CategoryDTO c = categoryBLL.getCategoryById(id);
                    if (c != null) {
                        new CategoryDetailDialog((JFrame) SwingUtilities.getWindowAncestor(this), c).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để xem chi tiết!");
                }
                break;
        }
    }

    private void performSearch() {
        String keyword = searchPanel.txtSearchForm.getText().toLowerCase();
        String type = (String) searchPanel.cbxChoose.getSelectedItem();
        int statusFilter = cbbStatusFilter.getSelectedIndex();

        ArrayList<CategoryDTO> result = new ArrayList<>();
        for (CategoryDTO c : categoryBLL.getCategoryList()) {
            boolean matchKey = false;
            String name = (c.getCategoryName() != null) ? c.getCategoryName().toLowerCase() : "";
            String strId = String.valueOf(c.getCategoryId());

            switch (type) {
                case "Tên danh mục": if (name.contains(keyword)) matchKey = true; break;
                case "Mã loại": if (strId.contains(keyword)) matchKey = true; break;
                default: if (name.contains(keyword) || strId.contains(keyword)) matchKey = true; break;
            }

            boolean matchStatus = true;
            if (statusFilter == 1 && c.getStatus() == 0) matchStatus = false;
            if (statusFilter == 2 && c.getStatus() == 1) matchStatus = false;

            if (matchKey && matchStatus) result.add(c);
        }
        loadDataTable(result);
    }

    private void refreshData() {
        loadDataTable(categoryBLL.getCategoryList());
    }

    private void resetForm() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        cbbStatusFilter.setSelectedIndex(0);
        refreshData();
    }

    public void loadDataTable(ArrayList<CategoryDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (CategoryDTO c : list) {
                tableModel.addRow(new Object[]{
                        c.getCategoryId(), c.getCategoryName(), c.getDescription(),
                        c.getStatus() == 1 ? "Hoạt động" : "Ngừng hoạt động"
                });
            }
        }
    }

    public void exportExcel() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Lưu file Excel");
        if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("DanhMuc");
                Row header = sheet.createRow(0);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);

                String[] columns = {"Mã loại", "Tên danh mục", "Mô tả", "Trạng thái"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(style);
                }

                ArrayList<CategoryDTO> list = categoryBLL.getCategoryList();
                for (int i = 0; i < list.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    CategoryDTO c = list.get(i);
                    row.createCell(0).setCellValue(c.getCategoryId());
                    row.createCell(1).setCellValue(c.getCategoryName());
                    row.createCell(2).setCellValue(c.getDescription() == null ? "" : c.getDescription());
                    row.createCell(3).setCellValue(c.getStatus() == 1 ? "Hoạt động" : "Ngừng hoạt động");
                }

                for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
                File f = jf.getSelectedFile();
                if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");
                try (FileOutputStream out = new FileOutputStream(f)) { workbook.write(out); }
                JOptionPane.showMessageDialog(this, "Xuất thành công!");
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage()); }
        }
    }

    public void importExcel() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Chọn file Excel");
        if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (FileInputStream fis = new FileInputStream(jf.getSelectedFile()); Workbook wb = new XSSFWorkbook(fis)) {
                Sheet sheet = wb.getSheetAt(0);
                int count = 0;
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    CategoryDTO c = new CategoryDTO();
                    if (row.getCell(1) != null) c.setCategoryName(row.getCell(1).getStringCellValue());
                    if (row.getCell(2) != null) c.setDescription(row.getCell(2).getStringCellValue());
                    c.setStatus(1);

                    if (categoryBLL.add(c).contains("thành công")) count++;
                }
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " danh mục!");
                refreshData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi nhập: " + e.getMessage()); }
        }
    }

    class CategoryDetailDialog extends JDialog {
        public CategoryDetailDialog(JFrame parent, CategoryDTO data) {
            super(parent, "CHI TIẾT DANH MỤC", true);
            setSize(500, 350);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            pnlHeader.setPreferredSize(new Dimension(0, 50));
            JLabel lblTitle = new JLabel("CHI TIẾT DANH MỤC");
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlBody = new JPanel(new GridLayout(3, 1, 0, 15));
            pnlBody.setBorder(new EmptyBorder(20, 40, 20, 40));
            pnlBody.setBackground(Color.WHITE);

            addReadOnlyField(pnlBody, "Tên danh mục", data.getCategoryName());
            addReadOnlyField(pnlBody, "Mô tả", data.getDescription());
            addReadOnlyField(pnlBody, "Trạng thái", data.getStatus() == 1 ? "Hoạt động" : "Ngừng hoạt động");

            add(pnlBody, BorderLayout.CENTER);

            JPanel pnlFooter = new JPanel();
            pnlFooter.setBackground(Color.WHITE);
            pnlFooter.setBorder(new EmptyBorder(0, 0, 20, 0));
            JButton btnCancel = new JButton("Đóng");
            btnCancel.setBackground(new Color(220, 53, 69));
            btnCancel.setForeground(Color.WHITE);
            btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnCancel.setPreferredSize(new Dimension(120, 40));
            btnCancel.setFocusPainted(false);
            btnCancel.addActionListener(e -> dispose());
            pnlFooter.add(btnCancel);
            add(pnlFooter, BorderLayout.SOUTH);
        }

        private void addReadOnlyField(JPanel pnl, String title, String content) {
            JPanel p = new JPanel(new BorderLayout(0, 5));
            p.setBackground(Color.WHITE);
            JLabel lbl = new JLabel(title);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JTextField txt = new JTextField(content);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txt.setEditable(false);
            txt.setBackground(new Color(245, 245, 245));
            txt.setPreferredSize(new Dimension(0, 35));
            txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)), new EmptyBorder(0, 10, 0, 10)));
            p.add(lbl, BorderLayout.NORTH);
            p.add(txt, BorderLayout.CENTER);
            pnl.add(p);
        }
    }

    class CategoryDialog extends JDialog {
        JTextField txtName, txtDesc;
        JComboBox<String> cbStatus;

        public CategoryDialog(JFrame parent, String title, CategoryDTO data) {
            super(parent, title, true);
            setSize(500, 400); // Tăng chiều cao để chứa thêm combobox
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            // Tăng số dòng lên 3 để chứa Trạng thái
            JPanel pnlCenter = new JPanel(new GridLayout(3, 1, 0, 20));
            pnlCenter.setBorder(new EmptyBorder(20, 40, 20, 40));
            pnlCenter.setBackground(Color.WHITE);

            txtName = new JTextField();
            txtDesc = new JTextField();
            cbStatus = new JComboBox<>(new String[]{"Hoạt động", "Ngừng hoạt động"});

            addInput(pnlCenter, "Tên danh mục:", txtName);
            addInput(pnlCenter, "Mô tả chi tiết:", txtDesc);
            addInput(pnlCenter, "Trạng thái:", cbStatus); // Bổ sung trạng thái

            if (data != null) {
                txtName.setText(data.getCategoryName());
                txtDesc.setText(data.getDescription());
                cbStatus.setSelectedIndex(data.getStatus() == 1 ? 0 : 1); // 0 là Hoạt động, 1 là Ngừng
            }

            add(pnlCenter, BorderLayout.CENTER);

            JPanel pnlBottom = new JPanel();
            pnlBottom.setBackground(Color.WHITE);
            pnlBottom.setBorder(new EmptyBorder(0,0,10,0));
            JButton btnSave = new JButton("Lưu");
            btnSave.setBackground(new Color(13, 110, 253));
            btnSave.setForeground(Color.WHITE);
            btnSave.setPreferredSize(new Dimension(100, 35));
            pnlBottom.add(btnSave);
            add(pnlBottom, BorderLayout.SOUTH);

            btnSave.addActionListener(e -> {
                String name = txtName.getText();
                String desc = txtDesc.getText();
                int status = cbStatus.getSelectedIndex() == 0 ? 1 : 0;

                if (data == null) {
                    CategoryDTO newCategory = new CategoryDTO(0, name, desc, status);
                    JOptionPane.showMessageDialog(this, categoryBLL.add(newCategory));
                } else {
                    data.setCategoryName(name);
                    data.setDescription(desc);
                    data.setStatus(status); // Set trạng thái trước khi update
                    JOptionPane.showMessageDialog(this, categoryBLL.update(data));
                }
                dispose();
            });
        }

        // Đổi JTextField thành JComponent để nhận cả JTextField và JComboBox
        private void addInput(JPanel p, String label, JComponent field) {
            JPanel item = new JPanel(new BorderLayout(0, 5));
            item.setBackground(Color.WHITE);
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            item.add(lbl, BorderLayout.NORTH);
            field.setPreferredSize(new Dimension(0, 35));
            item.add(field, BorderLayout.CENTER);
            p.add(item);
        }
    }
}
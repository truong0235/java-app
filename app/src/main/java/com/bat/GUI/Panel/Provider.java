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
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bat.BLL.ProviderBLL;
import com.bat.DTO.ProviderDTO;
import com.bat.GUI.Main;
import com.bat.GUI.component.ButtonToolbar;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;

public class Provider extends JPanel implements ActionListener {
    private Main main;
    private ProviderBLL providerBLL = new ProviderBLL();
    private DefaultTableModel tableModel;
    private JTable table;

    private IntegratedSearch searchPanel;
    private MenuFunction menuFunction;
    private JComboBox<String> cbbAddressFilter;

    public Provider(Main main) {
        this.main = main;
        initComponent();
        loadDataTable(providerBLL.getProviderList());
        loadFilterData();
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

        JLabel titleLabel = new JLabel("Quản lý nhà cung cấp");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Danh sách đối tác cung cấp hàng hóa");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        String[] navButtons = {"create", "update", "delete", "detail", "export"};
        menuFunction = new MenuFunction(navButtons);

        for (String btnKey : navButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            if(btn != null) {
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

        String[] searchOptions = {"Tất cả", "Tên NCC", "SĐT", "Email", "Địa chỉ"};
        searchPanel = new IntegratedSearch(searchOptions);
        searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập nội dung tìm kiếm...");
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

        JPanel addrPn = new JPanel(new GridLayout(2, 1));
        addrPn.setBackground(Color.WHITE);
        JLabel lblAddr = new JLabel("Lọc theo khu vực:");
        lblAddr.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cbbAddressFilter = new JComboBox<>();
        cbbAddressFilter.addItem("Tất cả");
        cbbAddressFilter.addActionListener(e -> performSearch());

        addrPn.add(lblAddr);
        addrPn.add(cbbAddressFilter);

        panel.add(addrPn);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));

        String[] columns = {"ID", "Tên nhà cung cấp", "Số điện thoại", "Email", "Địa chỉ"};
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
        col.getColumn(0).setPreferredWidth(50); col.getColumn(0).setCellRenderer(centerRenderer);
        col.getColumn(1).setPreferredWidth(250);
        col.getColumn(2).setPreferredWidth(120); col.getColumn(2).setCellRenderer(centerRenderer);
        col.getColumn(3).setPreferredWidth(200);
        col.getColumn(4).setPreferredWidth(200);

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
                new ProviderDialog((JFrame) SwingUtilities.getWindowAncestor(this), "THÊM NHÀ CUNG CẤP", null).setVisible(true);
                refreshData();
                break;
            case "update":
                int rowEdit = table.getSelectedRow();
                if (rowEdit != -1) {
                    try {
                        int id = Integer.parseInt(table.getValueAt(rowEdit, 0).toString());
                        ProviderDTO p = getProviderById(id);
                        if(p != null) {
                            new ProviderDialog((JFrame) SwingUtilities.getWindowAncestor(this), "CẬP NHẬT", p).setVisible(true);
                            refreshData();
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi lấy dữ liệu: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp cần sửa!");
                }
                break;
            case "delete":
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xóa!");
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa " + selectedRows.length + " nhà cung cấp?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        int success = 0;
                        for (int i : selectedRows) {
                            int id = Integer.parseInt(table.getValueAt(i, 0).toString());
                            String msg = providerBLL.delete(id);
                            if(msg.contains("thành công")) success++;
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
                    try {
                        int id = Integer.parseInt(table.getValueAt(rowDetail, 0).toString());
                        ProviderDTO p = getProviderById(id);
                        if (p != null) {
                            new ProviderDetailDialog((JFrame) SwingUtilities.getWindowAncestor(this), p).setVisible(true);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhà cung cấp để xem chi tiết!");
                }
                break;
        }
    }

    private void performSearch() {
        String keyword = searchPanel.txtSearchForm.getText().toLowerCase();
        String type = (String) searchPanel.cbxChoose.getSelectedItem();
        String addrFilter = (cbbAddressFilter.getSelectedItem() != null) ? (String) cbbAddressFilter.getSelectedItem() : "Tất cả";

        ArrayList<ProviderDTO> result = new ArrayList<>();
        ArrayList<ProviderDTO> list = providerBLL.getProviderList();

        for (ProviderDTO p : list) {
            boolean matchKey = false;
            String name = (p.getProviderName() != null) ? p.getProviderName().toLowerCase() : "";
            String phone = (p.getPhone() != null) ? p.getPhone().toLowerCase() : "";
            String email = (p.getEmail() != null) ? p.getEmail().toLowerCase() : "";
            String address = (p.getAddress() != null) ? p.getAddress().toLowerCase() : "";

            switch (type) {
                case "Tên NCC": if (name.contains(keyword)) matchKey = true; break;
                case "SĐT": if (phone.contains(keyword)) matchKey = true; break;
                case "Email": if (email.contains(keyword)) matchKey = true; break;
                case "Địa chỉ": if (address.contains(keyword)) matchKey = true; break;
                default:
                    if (name.contains(keyword) || phone.contains(keyword) || email.contains(keyword) || address.contains(keyword)) matchKey = true;
                    break;
            }

            boolean matchAddr = addrFilter.equals("Tất cả") || address.contains(addrFilter.toLowerCase());

            if (matchKey && matchAddr) result.add(p);
        }
        loadDataTable(result);
    }

    private String extractCity(String address) {
        if (address == null || address.trim().isEmpty()) return "Khác";
        String[] parts = address.split(",");
        if (parts.length > 0) {
            return parts[parts.length - 1].trim();
        }
        return address;
    }

    private void loadFilterData() {
        cbbAddressFilter.removeAllItems();
        cbbAddressFilter.addItem("Tất cả");

        Set<String> cities = new HashSet<>();
        for (ProviderDTO p : providerBLL.getProviderList()) {
            if (p.getAddress() != null) {
                cities.add(extractCity(p.getAddress()));
            }
        }

        for (String city : cities) {
            cbbAddressFilter.addItem(city);
        }
    }

    private void refreshData() {
        loadDataTable(providerBLL.getProviderList());
        loadFilterData();
    }

    private void resetForm() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        cbbAddressFilter.setSelectedIndex(0);
        refreshData();
    }

    public void loadDataTable(ArrayList<ProviderDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (ProviderDTO p : list) {
                tableModel.addRow(new Object[]{p.getProviderId(), p.getProviderName(), p.getPhone(), p.getEmail(), p.getAddress()});
            }
        }
    }

    private ProviderDTO getProviderById(int id) {
        for(ProviderDTO p : providerBLL.getProviderList()) {
            if(p.getProviderId() == id) return p;
        }
        return null;
    }

    public void exportExcel() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Lưu file Excel");
        if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Providers");
                Row header = sheet.createRow(0);

                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                style.setAlignment(HorizontalAlignment.CENTER);

                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(tableModel.getColumnName(i));
                    cell.setCellStyle(style);
                }
                for (int i = 0; i < table.getRowCount(); i++) {
                    Row row = sheet.createRow(i + 1);
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        Object val = table.getValueAt(i, j);
                        row.createCell(j).setCellValue(val != null ? val.toString() : "");
                    }
                }
                for(int i=0; i<tableModel.getColumnCount(); i++) sheet.autoSizeColumn(i);

                File f = jf.getSelectedFile();
                if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");
                try (FileOutputStream out = new FileOutputStream(f)) { workbook.write(out); }
                JOptionPane.showMessageDialog(this, "Xuất thành công!");
            } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage()); }
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

                    String name = "";
                    String phone = "";
                    String email = "";
                    String address = "";

                    if(row.getCell(1) != null) name = row.getCell(1).getStringCellValue();
                    if(row.getCell(2) != null) {
                        try {
                            phone = String.valueOf((long) row.getCell(2).getNumericCellValue());
                            if(!phone.startsWith("0")) phone = "0" + phone;
                        } catch (Exception e) {
                            phone = row.getCell(2).getStringCellValue();
                        }
                    }
                    if(row.getCell(3) != null) email = row.getCell(3).getStringCellValue();
                    if(row.getCell(4) != null) address = row.getCell(4).getStringCellValue();

                    ProviderDTO p = new ProviderDTO(0, name, address, phone, email);
                    String result = providerBLL.add(p);
                    if(result.contains("thành công")) count++;
                }
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " nhà cung cấp!");
                refreshData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi nhập: " + e.getMessage()); }
        }
    }
    class ProviderDetailDialog extends JDialog {
        public ProviderDetailDialog(JFrame parent, ProviderDTO data) {
            super(parent, "XEM CHI TIẾT NHÀ CUNG CẤP", true);
            setSize(600, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            pnlHeader.setPreferredSize(new Dimension(0, 50));
            JLabel lblTitle = new JLabel("XEM NHÀ CUNG CẤP");
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlBody = new JPanel(new GridLayout(4, 1, 0, 15));
            pnlBody.setBorder(new EmptyBorder(20, 40, 20, 40));
            pnlBody.setBackground(Color.WHITE);

            addReadOnlyField(pnlBody, "Tên nhà cung cấp", data.getProviderName());
            addReadOnlyField(pnlBody, "Số điện thoại", data.getPhone());
            addReadOnlyField(pnlBody, "Email", data.getEmail());
            addReadOnlyField(pnlBody, "Địa chỉ", data.getAddress());

            add(pnlBody, BorderLayout.CENTER);

            JPanel pnlFooter = new JPanel();
            pnlFooter.setBackground(Color.WHITE);
            pnlFooter.setBorder(new EmptyBorder(0, 0, 20, 0));

            JButton btnCancel = new JButton("Huỷ bỏ");
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
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    new EmptyBorder(0, 10, 0, 10)
            ));

            p.add(lbl, BorderLayout.NORTH);
            p.add(txt, BorderLayout.CENTER);
            pnl.add(p);
        }
    }

    class ProviderDialog extends JDialog {
        JTextField txtName, txtPhone, txtEmail, txtAddress;

        public ProviderDialog(JFrame parent, String title, ProviderDTO data) {
            super(parent, title, true);
            setSize(600, 400);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlCenter = new JPanel(new GridLayout(4, 2, 10, 20));
            pnlCenter.setBorder(new EmptyBorder(20, 40, 20, 40));
            pnlCenter.setBackground(Color.WHITE);

            txtName = new JTextField();
            txtPhone = new JTextField();
            txtEmail = new JTextField();
            txtAddress = new JTextField();

            addInput(pnlCenter, "Tên NCC:", txtName);
            addInput(pnlCenter, "SĐT:", txtPhone);
            addInput(pnlCenter, "Email:", txtEmail);
            addInput(pnlCenter, "Địa chỉ:", txtAddress);

            if(data != null) {
                txtName.setText(data.getProviderName());
                txtPhone.setText(data.getPhone());
                txtEmail.setText(data.getEmail());
                txtAddress.setText(data.getAddress());
            }

            add(pnlCenter, BorderLayout.CENTER);

            JPanel pnlBottom = new JPanel();
            pnlBottom.setBackground(Color.WHITE);
            JButton btnSave = new JButton("Lưu");
            btnSave.setBackground(new Color(13, 110, 253));
            btnSave.setForeground(Color.WHITE);
            btnSave.setPreferredSize(new Dimension(100, 35));
            pnlBottom.add(btnSave);
            add(pnlBottom, BorderLayout.SOUTH);

            btnSave.addActionListener(e -> {
                String name = txtName.getText();
                String phone = txtPhone.getText();
                String email = txtEmail.getText();
                String address = txtAddress.getText();

                if(name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên nhà cung cấp không được để trống!");
                    return;
                }

                if(data == null) {
                    ProviderDTO newProvider = new ProviderDTO(0, name, address, phone, email);
                    JOptionPane.showMessageDialog(this, providerBLL.add(newProvider));
                } else {
                    data.setProviderName(name);
                    data.setPhone(phone);
                    data.setEmail(email);
                    data.setAddress(address);
                    JOptionPane.showMessageDialog(this, providerBLL.update(data));
                }
                refreshData();
                dispose();
            });
        }


        private void addInput(JPanel p, String label, JTextField field) {
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            p.add(lbl);
            field.setPreferredSize(new Dimension(0, 30));
            p.add(field);
        }
    }
}
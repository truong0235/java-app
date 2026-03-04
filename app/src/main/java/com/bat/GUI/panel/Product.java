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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

import com.bat.BLL.ProductBLL;
import com.bat.DTO.ProductDTO;
import com.bat.GUI.Main;
import com.bat.GUI.component.ButtonToolbar;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;

public class Product extends JPanel implements ActionListener {
    private Main main;
    private ProductBLL productBLL = new ProductBLL();
    private DefaultTableModel tableModel;
    private JTable table;

    private IntegratedSearch searchPanel;
    private MenuFunction menuFunction;
    private JComboBox<String> cbbBrandFilter;
    private final DecimalFormat priceFormatter = new DecimalFormat("###,###,###");

    public Product(Main main) {
        this.main = main;
        initComponent();
        loadDataTable(productBLL.getProductList());
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

        JLabel titleLabel = new JLabel("Quản lý sản phẩm");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Thông tin kho hàng và danh mục sản phẩm");
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

        String[] searchOptions = {"Tất cả", "Tên sản phẩm", "Tác giả", "Mã SP"};
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

        JPanel brandPn = new JPanel(new GridLayout(2, 1));
        brandPn.setBackground(Color.WHITE);
        JLabel lblBrand = new JLabel("Lọc theo thương hiệu:");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cbbBrandFilter = new JComboBox<>();
        cbbBrandFilter.addItem("Tất cả");
        cbbBrandFilter.addActionListener(e -> performSearch());

        brandPn.add(lblBrand);
        brandPn.add(cbbBrandFilter);

        panel.add(brandPn);
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));

        // Bổ sung "Hình ảnh" vào bảng
        String[] columns = {"ID", "Tên SP", "Thương hiệu", "Năm XB", "Tác giả", "Ngôn ngữ", "Loại", "Giá", "Số lượng", "Trạng thái", "Hình ảnh"};
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

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // Chỉnh lại kích thước 11 cột
        TableColumnModel col = table.getColumnModel();
        col.getColumn(0).setPreferredWidth(30); col.getColumn(0).setCellRenderer(centerRenderer);
        col.getColumn(1).setPreferredWidth(140); // Tên SP
        col.getColumn(2).setPreferredWidth(90);  // Thương hiệu
        col.getColumn(3).setPreferredWidth(50);  col.getColumn(3).setCellRenderer(centerRenderer); // Năm XB
        col.getColumn(4).setPreferredWidth(100); // Tác giả
        col.getColumn(5).setPreferredWidth(60);  // Ngôn ngữ
        col.getColumn(6).setPreferredWidth(40);  col.getColumn(6).setCellRenderer(centerRenderer); // Mã loại
        col.getColumn(7).setPreferredWidth(70);  col.getColumn(7).setCellRenderer(rightRenderer); // Giá
        col.getColumn(8).setPreferredWidth(50);  col.getColumn(8).setCellRenderer(centerRenderer); // Số lượng
        col.getColumn(9).setPreferredWidth(70);  col.getColumn(9).setCellRenderer(centerRenderer); // Trạng thái
        col.getColumn(10).setPreferredWidth(80); // Hình ảnh (Link Pic)

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
                new ProductDialog((JFrame) SwingUtilities.getWindowAncestor(this), "THÊM SẢN PHẨM", null).setVisible(true);
                refreshData();
                break;
            case "update":
                int rowEdit = table.getSelectedRow();
                if (rowEdit != -1) {
                    int id = Integer.parseInt(table.getValueAt(rowEdit, 0).toString());
                    ProductDTO p = productBLL.getProductById(id);
                    if (p != null) {
                        new ProductDialog((JFrame) SwingUtilities.getWindowAncestor(this), "CẬP NHẬT SẢN PHẨM", p).setVisible(true);
                        refreshData();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần sửa!");
                }
                break;
            case "delete":
                int[] selectedRows = table.getSelectedRows();
                if (selectedRows.length == 0) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!");
                } else {
                    if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa " + selectedRows.length + " sản phẩm?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        int success = 0;
                        for (int i : selectedRows) {
                            int id = Integer.parseInt(table.getValueAt(i, 0).toString());
                            String msg = productBLL.delete(id);
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
                    ProductDTO p = productBLL.getProductById(id);
                    if (p != null) {
                        new ProductDetailDialog((JFrame) SwingUtilities.getWindowAncestor(this), p).setVisible(true);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xem chi tiết!");
                }
                break;
        }
    }

    private void performSearch() {
        String keyword = searchPanel.txtSearchForm.getText().toLowerCase();
        String type = (String) searchPanel.cbxChoose.getSelectedItem();
        String brandFilter = (cbbBrandFilter.getSelectedItem() != null) ? (String) cbbBrandFilter.getSelectedItem() : "Tất cả";

        ArrayList<ProductDTO> result = new ArrayList<>();
        for (ProductDTO p : productBLL.getProductList()) {
            boolean matchKey = false;
            String name = (p.getProductName() != null) ? p.getProductName().toLowerCase() : "";
            String author = (p.getAuthor() != null) ? p.getAuthor().toLowerCase() : "";
            String strId = String.valueOf(p.getProductId());

            switch (type) {
                case "Tên sản phẩm": if (name.contains(keyword)) matchKey = true; break;
                case "Tác giả": if (author.contains(keyword)) matchKey = true; break;
                case "Mã SP": if (strId.contains(keyword)) matchKey = true; break;
                default: if (name.contains(keyword) || author.contains(keyword) || strId.contains(keyword)) matchKey = true; break;
            }

            boolean matchBrand = brandFilter.equals("Tất cả") || (p.getPublisher() != null && p.getPublisher().equals(brandFilter));

            if (matchKey && matchBrand) result.add(p);
        }
        loadDataTable(result);
    }

    private void loadFilterData() {
        ActionListener[] listeners = cbbBrandFilter.getActionListeners();
        for (ActionListener al : listeners) cbbBrandFilter.removeActionListener(al);

        cbbBrandFilter.removeAllItems();
        cbbBrandFilter.addItem("Tất cả");

        Set<String> brands = new HashSet<>();
        for (ProductDTO p : productBLL.getProductList()) {
            if (p.getPublisher() != null && !p.getPublisher().trim().isEmpty()) {
                brands.add(p.getPublisher());
            }
        }
        for (String brand : brands) cbbBrandFilter.addItem(brand);

        for (ActionListener al : listeners) cbbBrandFilter.addActionListener(al);
    }

    private void refreshData() {
        loadDataTable(productBLL.getProductList());
        loadFilterData();
    }

    private void resetForm() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        cbbBrandFilter.setSelectedIndex(0);
        refreshData();
    }

    public void loadDataTable(ArrayList<ProductDTO> list) {
        tableModel.setRowCount(0);
        if (list != null) {
            for (ProductDTO p : list) {
                tableModel.addRow(new Object[]{
                        p.getProductId(), p.getProductName(), p.getPublisher(), p.getPublishYear(),
                        p.getAuthor(), p.getLanguage(), p.getCategoryId(),
                        p.getPrice() != null ? priceFormatter.format(p.getPrice()) : "0",
                        p.getQuantity(), p.getStatus() == 1 ? "Đang bán" : "Ngừng bán",
                        p.getPic() // Đẩy cột Hình ảnh ra bảng
                });
            }
        }
    }

    public void exportExcel() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Lưu file Excel");
        if (jf.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("SanPham");
                Row header = sheet.createRow(0);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);

                String[] columns = {"Mã SP", "Tên SP", "Thương hiệu", "Năm XB", "Tác giả", "Ngôn ngữ", "Giá", "Số lượng", "Mã Loại", "Hình ảnh"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = header.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(style);
                }

                ArrayList<ProductDTO> list = productBLL.getProductList();
                for (int i = 0; i < list.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    ProductDTO p = list.get(i);
                    row.createCell(0).setCellValue(p.getProductId());
                    row.createCell(1).setCellValue(p.getProductName());
                    row.createCell(2).setCellValue(p.getPublisher());
                    row.createCell(3).setCellValue(p.getPublishYear());
                    row.createCell(4).setCellValue(p.getAuthor());
                    row.createCell(5).setCellValue(p.getLanguage());
                    row.createCell(6).setCellValue(p.getPrice() != null ? p.getPrice().doubleValue() : 0);
                    row.createCell(7).setCellValue(p.getQuantity());
                    row.createCell(8).setCellValue(p.getCategoryId());
                    row.createCell(9).setCellValue(p.getPic() == null ? "" : p.getPic());
                }

                for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
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

                    ProductDTO p = new ProductDTO();
                    if (row.getCell(1) != null) p.setProductName(row.getCell(1).getStringCellValue());
                    if (row.getCell(2) != null) p.setPublisher(row.getCell(2).getStringCellValue());
                    if (row.getCell(3) != null) p.setPublishYear((int) row.getCell(3).getNumericCellValue());
                    if (row.getCell(4) != null) p.setAuthor(row.getCell(4).getStringCellValue());
                    if (row.getCell(5) != null) p.setLanguage(row.getCell(5).getStringCellValue());
                    if (row.getCell(6) != null) p.setPrice(BigDecimal.valueOf(row.getCell(6).getNumericCellValue()));
                    if (row.getCell(7) != null) p.setQuantity((int) row.getCell(7).getNumericCellValue());
                    if (row.getCell(8) != null) p.setCategoryId((int) row.getCell(8).getNumericCellValue());
                    if (row.getCell(9) != null) p.setPic(row.getCell(9).getStringCellValue());

                    p.setStatus(1);

                    if (productBLL.add(p).contains("thành công")) count++;
                }
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " sản phẩm!");
                refreshData();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi nhập: " + e.getMessage()); }
        }
    }

    class ProductDetailDialog extends JDialog {
        public ProductDetailDialog(JFrame parent, ProductDTO data) {
            super(parent, "CHI TIẾT SẢN PHẨM", true);
            setSize(700, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            pnlHeader.setPreferredSize(new Dimension(0, 50));
            JLabel lblTitle = new JLabel("CHI TIẾT SẢN PHẨM");
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlBody = new JPanel(new GridLayout(5, 2, 15, 15));
            pnlBody.setBorder(new EmptyBorder(20, 40, 20, 40));
            pnlBody.setBackground(Color.WHITE);

            addReadOnlyField(pnlBody, "Tên SP", data.getProductName());
            addReadOnlyField(pnlBody, "Thương hiệu", data.getPublisher());
            addReadOnlyField(pnlBody, "Năm XB", String.valueOf(data.getPublishYear()));
            addReadOnlyField(pnlBody, "Tác giả", data.getAuthor());
            addReadOnlyField(pnlBody, "Ngôn ngữ", data.getLanguage());
            addReadOnlyField(pnlBody, "Mã loại", String.valueOf(data.getCategoryId()));
            addReadOnlyField(pnlBody, "Giá bán", (data.getPrice() != null ? priceFormatter.format(data.getPrice()) : "0") + " VNĐ");
            addReadOnlyField(pnlBody, "Số lượng", String.valueOf(data.getQuantity()));
            addReadOnlyField(pnlBody, "Trạng thái", data.getStatus() == 1 ? "Đang kinh doanh" : "Ngừng bán");
            addReadOnlyField(pnlBody, "Hình ảnh (Link)", data.getPic());

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
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            JTextField txt = new JTextField(content);
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            txt.setEditable(false);
            txt.setBackground(new Color(245, 245, 245));
            txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)), new EmptyBorder(0, 10, 0, 10)));
            p.add(lbl, BorderLayout.NORTH);
            p.add(txt, BorderLayout.CENTER);
            pnl.add(p);
        }
    }

    class ProductDialog extends JDialog {
        JTextField txtName, txtBrand, txtYear, txtAuthor, txtLang, txtPrice, txtQty, txtCat, txtPic;

        public ProductDialog(JFrame parent, String title, ProductDTO data) {
            super(parent, title, true);
            setSize(700, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlCenter = new JPanel(new GridLayout(5, 2, 15, 10));
            pnlCenter.setBorder(new EmptyBorder(20, 40, 20, 40));
            pnlCenter.setBackground(Color.WHITE);

            txtName = new JTextField(); txtBrand = new JTextField();
            txtYear = new JTextField(); txtAuthor = new JTextField();
            txtLang = new JTextField(); txtCat = new JTextField();
            txtPrice = new JTextField(); txtQty = new JTextField();
            txtPic = new JTextField();

            addInput(pnlCenter, "Tên sản phẩm:", txtName);
            addInput(pnlCenter, "Thương hiệu:", txtBrand);
            addInput(pnlCenter, "Năm xuất bản:", txtYear);
            addInput(pnlCenter, "Tác giả:", txtAuthor);
            addInput(pnlCenter, "Ngôn ngữ:", txtLang);
            addInput(pnlCenter, "Mã loại:", txtCat);
            addInput(pnlCenter, "Giá bán:", txtPrice);
            addInput(pnlCenter, "Số lượng tồn:", txtQty);
            addInput(pnlCenter, "Đường dẫn ảnh:", txtPic);

            if (data != null) {
                txtName.setText(data.getProductName());
                txtBrand.setText(data.getPublisher());
                txtYear.setText(String.valueOf(data.getPublishYear()));
                txtAuthor.setText(data.getAuthor());
                txtLang.setText(data.getLanguage());
                txtCat.setText(String.valueOf(data.getCategoryId()));
                txtPrice.setText(data.getPrice() != null ? data.getPrice().toString() : "0");
                txtQty.setText(String.valueOf(data.getQuantity()));
                txtPic.setText(data.getPic());
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
                try {
                    String name = txtName.getText();
                    String brand = txtBrand.getText();
                    int year = Integer.parseInt(txtYear.getText().isEmpty() ? "0" : txtYear.getText());
                    String author = txtAuthor.getText();
                    String lang = txtLang.getText();
                    int cat = Integer.parseInt(txtCat.getText().isEmpty() ? "0" : txtCat.getText());
                    BigDecimal price = new BigDecimal(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText());
                    int qty = Integer.parseInt(txtQty.getText().isEmpty() ? "0" : txtQty.getText());
                    String pic = txtPic.getText();

                    if (data == null) {
                        ProductDTO newProduct = new ProductDTO(0, name, pic, cat, brand, year, author, lang, price, qty, 1);
                        JOptionPane.showMessageDialog(this, productBLL.add(newProduct));
                    } else {
                        data.setProductName(name); data.setPublisher(brand);
                        data.setPublishYear(year); data.setAuthor(author);
                        data.setLanguage(lang); data.setCategoryId(cat);
                        data.setPrice(price); data.setQuantity(qty);
                        data.setPic(pic);
                        JOptionPane.showMessageDialog(this, productBLL.update(data));
                    }
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho Năm XB, Loại, Giá và Số lượng!");
                }
            });
        }

        private void addInput(JPanel p, String label, JTextField field) {
            JPanel item = new JPanel(new BorderLayout(0, 5));
            item.setBackground(Color.WHITE);
            JLabel lbl = new JLabel(label);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            item.add(lbl, BorderLayout.NORTH);
            field.setPreferredSize(new Dimension(0, 30));
            item.add(field, BorderLayout.CENTER);
            p.add(item);
        }
    }
}
package com.bat.GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.bat.BLL.CategoryBLL;
import com.bat.BLL.ProductBLL;
import com.bat.DTO.CategoryDTO;
import com.bat.DTO.ProductDTO;
import com.bat.GUI.Main;
import com.bat.GUI.component.ButtonToolbar;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;

public class Product extends JPanel implements ActionListener {
    private Main main;
    private ProductBLL productBLL = new ProductBLL();
    private CategoryBLL categoryBLL = new CategoryBLL();
    private DefaultTableModel tableModel;
    private JTable table;

    private IntegratedSearch searchPanel;
    private MenuFunction menuFunction;
    private JComboBox<String> cbbBrandFilter;
    private final DecimalFormat priceFormatter = new DecimalFormat("###,###,###");

    // Đã cấu hình chính xác đường dẫn theo cấu trúc: app/src/main/resources/image_product/
    private final String IMAGE_DIR;

    public Product(Main main) {
        this.main = main;

        // Thiết lập đường dẫn động để tương thích với cấu trúc "java-app-main\app\..."
        String userDir = System.getProperty("user.dir");
        if (userDir.endsWith("app")) {
            IMAGE_DIR = userDir + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "image_product" + File.separator;
        } else {
            IMAGE_DIR = userDir + File.separator + "app" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "image_product" + File.separator;
        }

        // Tự động tạo thư mục nếu chưa tồn tại
        File directory = new File(IMAGE_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

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
        JLabel lblBrand = new JLabel("Lọc theo nhà xuất bản:");
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

        String[] columns = {"Mã SP", "Tên SP", "Nhà xuất bản", "Thể loại", "Giá", "Số lượng"};
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

        TableColumnModel col = table.getColumnModel();
        col.getColumn(0).setPreferredWidth(30); col.getColumn(0).setCellRenderer(centerRenderer);
        col.getColumn(1).setPreferredWidth(160);
        col.getColumn(2).setPreferredWidth(100);
        col.getColumn(3).setPreferredWidth(100);  col.getColumn(3).setCellRenderer(centerRenderer);
        col.getColumn(4).setPreferredWidth(120); col.getColumn(4).setCellRenderer(centerRenderer);
        col.getColumn(5).setPreferredWidth(70);

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
                        p.getProductId(),
                        p.getProductName(), p.getPublisher(),
                        categoryBLL.getCategoryNameById(p.getCategoryId()),
                        p.getPrice() != null ? priceFormatter.format(p.getPrice()) : "0",
                        p.getQuantity()
                });
            }
        }
    }

    // Đã cập nhật để xuất ID thể loại và Hình ảnh
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

                String[] columns = {"Mã SP", "Tên SP", "Nhà xuất bản", "Năm XB", "Tác giả", "Ngôn ngữ", "Giá", "Số lượng", "Mã Thể loại", "Hình ảnh"};
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
                    row.createCell(8).setCellValue(p.getCategoryId()); // Xuất ID thay vì Tên Thể loại
                    row.createCell(9).setCellValue(p.getPic() == null ? "" : p.getPic()); // Xuất hình ảnh
                }

                for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
                File f = jf.getSelectedFile();
                if (!f.getName().endsWith(".xlsx")) f = new File(f.getAbsolutePath() + ".xlsx");
                try (FileOutputStream out = new FileOutputStream(f)) { workbook.write(out); }
                JOptionPane.showMessageDialog(this, "Xuất thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi xuất file: " + e.getMessage());
            }
        }
    }

    // Đã cập nhật sử dụng DataFormatter để chống lỗi "Cannot get a NUMERIC value from a STRING cell"
    public void importExcel() {
        JFileChooser jf = new JFileChooser();
        jf.setDialogTitle("Chọn file Excel");
        if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (FileInputStream fis = new FileInputStream(jf.getSelectedFile()); Workbook wb = new XSSFWorkbook(fis)) {
                Sheet sheet = wb.getSheetAt(0);
                int count = 0;

                DataFormatter formatter = new DataFormatter();

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    ProductDTO p = new ProductDTO();

                    if (row.getCell(1) != null) p.setProductName(formatter.formatCellValue(row.getCell(1)));
                    if (row.getCell(2) != null) p.setPublisher(formatter.formatCellValue(row.getCell(2)));

                    if (row.getCell(3) != null) {
                        String yearStr = formatter.formatCellValue(row.getCell(3)).replace(",", "");
                        if (!yearStr.trim().isEmpty()) {
                            p.setPublishYear((int) Double.parseDouble(yearStr));
                        }
                    }

                    if (row.getCell(4) != null) p.setAuthor(formatter.formatCellValue(row.getCell(4)));
                    if (row.getCell(5) != null) p.setLanguage(formatter.formatCellValue(row.getCell(5)));

                    if (row.getCell(6) != null) {
                        String priceStr = formatter.formatCellValue(row.getCell(6)).replace(",", "");
                        if (!priceStr.trim().isEmpty()) {
                            p.setPrice(BigDecimal.valueOf(Double.parseDouble(priceStr)));
                        }
                    }

                    if (row.getCell(7) != null) {
                        String qtyStr = formatter.formatCellValue(row.getCell(7)).replace(",", "");
                        if (!qtyStr.trim().isEmpty()) {
                            p.setQuantity((int) Double.parseDouble(qtyStr));
                        }
                    }

                    if (row.getCell(8) != null) {
                        String cateStr = formatter.formatCellValue(row.getCell(8)).replace(",", "");
                        if (!cateStr.trim().isEmpty()) {
                            p.setCategoryId((int) Double.parseDouble(cateStr));
                        }
                    }

                    if (row.getCell(9) != null) p.setPic(formatter.formatCellValue(row.getCell(9)));

                    p.setStatus(1);

                    if (productBLL.add(p).contains("thành công")) count++;
                }
                JOptionPane.showMessageDialog(this, "Đã nhập thành công " + count + " sản phẩm!");
                refreshData();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi nhập: " + e.getMessage());
            }
        }
    }

    // Tự động tìm và hiển thị ảnh
    private void displayImageToLabel(String path, JLabel label, int width, int height) {
        if (path != null && !path.trim().isEmpty()) {
            try {
                File file = new File(path);
                if (!file.exists()) {
                    file = new File(IMAGE_DIR + path);
                }

                if (file.exists()) {
                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(img));
                    label.setText("");
                } else {
                    label.setIcon(null);
                    label.setText("Ảnh không tồn tại");
                }
            } catch (Exception ex) {
                label.setIcon(null);
                label.setText("Lỗi tải ảnh");
            }
        } else {
            label.setIcon(null);
            label.setText("Chưa có ảnh");
        }
    }

    class ProductDetailDialog extends JDialog {
        public ProductDetailDialog(JFrame parent, ProductDTO data) {
            super(parent, "CHI TIẾT SẢN PHẨM", true);
            setSize(750, 500);
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

            JPanel pnlMain = new JPanel(new BorderLayout(20, 0));
            pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));
            pnlMain.setBackground(Color.WHITE);

            JPanel pnlInfo = new JPanel(new GridLayout(4, 2, 15, 15));
            pnlInfo.setBackground(Color.WHITE);

            addReadOnlyField(pnlInfo, "Tên SP", data.getProductName());
            addReadOnlyField(pnlInfo, "Nhà XB", data.getPublisher());
            addReadOnlyField(pnlInfo, "Năm XB", String.valueOf(data.getPublishYear()));
            addReadOnlyField(pnlInfo, "Tác giả", data.getAuthor());
            addReadOnlyField(pnlInfo, "Ngôn ngữ", data.getLanguage());
            addReadOnlyField(pnlInfo, "Thể loại", categoryBLL.getCategoryNameById(data.getCategoryId()));
            addReadOnlyField(pnlInfo, "Giá bán", (data.getPrice() != null ? priceFormatter.format(data.getPrice()) : "0") + " VNĐ");
            addReadOnlyField(pnlInfo, "Số lượng", String.valueOf(data.getQuantity()));

            pnlMain.add(pnlInfo, BorderLayout.CENTER);

            JPanel pnlImage = new JPanel(new BorderLayout());
            pnlImage.setBackground(Color.WHITE);
            pnlImage.setBorder(BorderFactory.createTitledBorder("Hình ảnh sản phẩm"));

            JLabel lblImage = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
            lblImage.setPreferredSize(new Dimension(200, 250));
            displayImageToLabel(data.getPic(), lblImage, 200, 250);
            pnlImage.add(lblImage, BorderLayout.CENTER);

            pnlMain.add(pnlImage, BorderLayout.EAST);
            add(pnlMain, BorderLayout.CENTER);

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
        JTextField txtName, txtBrand, txtYear, txtAuthor, txtLang, txtPrice, txtQty, txtCate;
        JComboBox<String> cbbCate;
        JLabel lblImagePreview;
        String selectedImagePath = "";
        List<CategoryDTO> categoryList;

        public ProductDialog(JFrame parent, String title, ProductDTO data) {
            super(parent, title, true);
            setSize(800, 500);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JPanel pnlHeader = new JPanel();
            pnlHeader.setBackground(new Color(13, 110, 253));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pnlHeader.add(lblTitle);
            add(pnlHeader, BorderLayout.NORTH);

            JPanel pnlBody = new JPanel(new BorderLayout(20, 0));
            pnlBody.setBorder(new EmptyBorder(20, 20, 20, 20));
            pnlBody.setBackground(Color.WHITE);

            JPanel pnlForm = new JPanel(new GridLayout(4, 2, 15, 10));
            pnlForm.setBackground(Color.WHITE);

            txtName = new JTextField(); txtBrand = new JTextField();
            txtYear = new JTextField(); txtAuthor = new JTextField();
            txtLang = new JTextField(); cbbCate = new JComboBox<>();
            txtPrice = new JTextField(); txtQty = new JTextField();

            addInput(pnlForm, "Tên sản phẩm:", txtName);
            addInput(pnlForm, "Nhà XB:", txtBrand);
            addInput(pnlForm, "Năm xuất bản:", txtYear);
            addInput(pnlForm, "Tác giả:", txtAuthor);
            addInput(pnlForm, "Ngôn ngữ:", txtLang);

            JPanel cateLbl = new JPanel(new BorderLayout(0, 5));
            cateLbl.setBackground(Color.WHITE);
            JLabel lbl = new JLabel("Thể loại");
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cateLbl.add(lbl, BorderLayout.NORTH);
            categoryList = categoryBLL.getCategoryList();
            for (CategoryDTO category : categoryList) {
                cbbCate.addItem(category.getCategoryName());
            }
            cbbCate.setPreferredSize(new Dimension(0, 30));
            cateLbl.add(cbbCate, BorderLayout.CENTER);
            pnlForm.add(cateLbl);

            addInput(pnlForm, "Giá bán:", txtPrice);

            pnlBody.add(pnlForm, BorderLayout.CENTER);

            JPanel pnlImgWrap = new JPanel(new BorderLayout(0, 10));
            pnlImgWrap.setBackground(Color.WHITE);
            pnlImgWrap.setBorder(BorderFactory.createTitledBorder("Ảnh sản phẩm"));

            lblImagePreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
            lblImagePreview.setPreferredSize(new Dimension(200, 250));
            lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JButton btnChooseImage = new JButton("Chọn ảnh...");
            btnChooseImage.setFocusPainted(false);
            btnChooseImage.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Chọn hình ảnh sản phẩm");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "jpeg", "png");
                fileChooser.setFileFilter(filter);

                fileChooser.setCurrentDirectory(new File(IMAGE_DIR));

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    selectedImagePath = file.getAbsolutePath();
                    displayImageToLabel(selectedImagePath, lblImagePreview, 200, 250);
                }
            });

            pnlImgWrap.add(lblImagePreview, BorderLayout.CENTER);
            pnlImgWrap.add(btnChooseImage, BorderLayout.SOUTH);

            pnlBody.add(pnlImgWrap, BorderLayout.EAST);
            add(pnlBody, BorderLayout.CENTER);

            if (data != null) {
                txtName.setText(data.getProductName());
                txtBrand.setText(data.getPublisher());
                txtYear.setText(String.valueOf(data.getPublishYear()));
                txtAuthor.setText(data.getAuthor());
                txtLang.setText(data.getLanguage());
                cbbCate.setSelectedItem(categoryBLL.getCategoryNameById(data.getCategoryId()));

                // ĐÃ XỬ LÝ ẨN SỐ .00 Ở ĐÂY
                txtPrice.setText(data.getPrice() != null ? data.getPrice().stripTrailingZeros().toPlainString() : "0");

                txtQty.setText(String.valueOf(data.getQuantity()));

                selectedImagePath = data.getPic();
                displayImageToLabel(selectedImagePath, lblImagePreview, 200, 250);
            }

            JPanel pnlBottom = new JPanel();
            pnlBottom.setBackground(Color.WHITE);
            pnlBottom.setBorder(new EmptyBorder(0,0,10,0));
            JButton btnSave = new JButton("Lưu thông tin");
            btnSave.setBackground(new Color(13, 110, 253));
            btnSave.setForeground(Color.WHITE);
            btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnSave.setPreferredSize(new Dimension(150, 40));
            pnlBottom.add(btnSave);
            add(pnlBottom, BorderLayout.SOUTH);

            btnSave.addActionListener(e -> {
                String validationError = validateInput();
                if (validationError != null) {
                    JOptionPane.showMessageDialog(this, validationError, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    String name = txtName.getText().trim();
                    String brand = txtBrand.getText().trim();
                    int year = Integer.parseInt(txtYear.getText().trim());
                    String author = txtAuthor.getText().trim();
                    String lang = txtLang.getText().trim();

                    int selectedIndex = cbbCate.getSelectedIndex();
                    int cat = categoryList.get(selectedIndex).getCategoryId();

                    BigDecimal price = new BigDecimal(txtPrice.getText().trim());

                    String picToSave = (data != null && data.getPic() != null) ? data.getPic() : "";

                    if (selectedImagePath != null && !selectedImagePath.isEmpty() && !selectedImagePath.equals(picToSave)) {
                        File sourceFile = new File(selectedImagePath);
                        if (sourceFile.exists()) {
                            String fileName = sourceFile.getName();
                            File destFile = new File(IMAGE_DIR + fileName);

                            if (!sourceFile.getAbsolutePath().equals(destFile.getAbsolutePath())) {
                                try {
                                    Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                } catch (Exception ex) {
                                    System.err.println("Lỗi copy ảnh: " + ex.getMessage());
                                }
                            }
                            picToSave = fileName;
                        }
                    }

                    if (data == null) {
                        ProductDTO newProduct = new ProductDTO();
                        newProduct.setProductName(name);
                        newProduct.setPublisher(brand);
                        newProduct.setPublishYear(year);
                        newProduct.setAuthor(author);
                        newProduct.setLanguage(lang);
                        newProduct.setCategoryId(cat);
                        newProduct.setPrice(price);
                        newProduct.setQuantity(0);
                        newProduct.setPic(picToSave);
                        newProduct.setStatus(1);
                        String result = productBLL.add(newProduct);
                        if (result.contains("thành công")) {
                            JOptionPane.showMessageDialog(this, result, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        data.setProductName(name);
                        data.setPublisher(brand);
                        data.setPublishYear(year);
                        data.setAuthor(author);
                        data.setLanguage(lang);
                        data.setCategoryId(cat);
                        data.setPrice(price);
                        data.setPic(picToSave);
                        String result = productBLL.update(data);
                        if (result.contains("thành công")) {
                            JOptionPane.showMessageDialog(this, result, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, result, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
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

        private String validateInput() {
            if (txtName.getText().trim().isEmpty()) {
                txtName.requestFocus();
                return "Tên sản phẩm không được để trống!";
            }

            if (txtBrand.getText().trim().isEmpty()) {
                txtBrand.requestFocus();
                return "Nhà xuất bản không được để trống!";
            }

            if (txtYear.getText().trim().isEmpty()) {
                txtYear.requestFocus();
                return "Năm xuất bản không được để trống!";
            }
            try {
                int year = Integer.parseInt(txtYear.getText().trim());
                if (year < 1000 || year > 2026) {
                    txtYear.requestFocus();
                    return "Năm xuất bản phải nằm trong khoảng 1000 - 2026!";
                }
            } catch (NumberFormatException e) {
                txtYear.requestFocus();
                return "Năm xuất bản phải là số nguyên hợp lệ!";
            }

            if (txtAuthor.getText().isEmpty()) {
                txtAuthor.requestFocus();
                return "Tên tác giả không được để trống!";
            }

            if (cbbCate.getSelectedIndex() < 0) {
                cbbCate.requestFocus();
                return "Vui lòng chọn thể loại sản phẩm!";
            }

            if (txtPrice.getText().trim().isEmpty()) {
                txtPrice.requestFocus();
                return "Giá bán không được để trống!";
            }
            try {
                BigDecimal price = new BigDecimal(txtPrice.getText().trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    txtPrice.requestFocus();
                    return "Giá bán phải lớn hơn 0!";
                }
                if (price.compareTo(new BigDecimal("999999999")) > 0) {
                    txtPrice.requestFocus();
                    return "Giá bán không được vượt quá 999,999,999!";
                }
            } catch (NumberFormatException e) {
                txtPrice.requestFocus();
                return "Giá bán phải là số hợp lệ (ví dụ: 50000 hoặc 50000.50)!";
            }

            return null;
        }
    }
}

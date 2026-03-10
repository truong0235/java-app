package com.bat.GUI.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.bat.BLL.CustomerBLL;
import com.bat.BLL.ExportBLL;
import com.bat.BLL.UserBLL;
import com.bat.DTO.CustomerDTO;
import com.bat.DTO.ExportReceiptDTO;
import com.bat.DTO.UserDTO;
import com.bat.GUI.Main;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;
import com.bat.GUI.dialog.AddExportDialog;
import com.bat.GUI.dialog.ReceiptDetailDialog;
import com.bat.utils.helper.ExcelExporter;
import com.toedter.calendar.JDateChooser;

public class Export extends JPanel implements ActionListener, ItemListener, KeyListener, PropertyChangeListener {
    UserBLL userBLL = new UserBLL();
    CustomerBLL customerBLL = new CustomerBLL();
    ExportBLL exportBLL = new ExportBLL();

    DefaultTableModel tableModel;
    JTable table;
    ArrayList<ExportReceiptDTO> exportList;
    
    IntegratedSearch searchPanel;
    MenuFunction menuFunction;
    JComboBox<String> customerCbx, userCbx;
    JDateChooser fromDateChooser, toDateChooser;
    Main main;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.of("vi", "VN"));

    public Export(Main main) {
        this.main = main;
        initComponent();
        exportList = exportBLL.getExportList();
        loadDataTable(exportList);
    }

    public void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] exportButtons = {"detail", "create", "delete", "export"};
        String[] exportSearchOptions = {"Tất cả", "Mã phiếu xuất", "Khách hàng", "Nhân viên xuất"};

        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setBackground(new Color(228, 238, 255));
        menuBar.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản lý phiếu xuất");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Danh sách phiếu xuất hàng hóa");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        menuFunction = new MenuFunction(exportButtons);
        for (String btnKey : exportButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            btn.setActionCommand(btnKey);
            btn.addActionListener(this);
        }
        
        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);
        
        if (exportSearchOptions != null) {
            searchPanel = new IntegratedSearch(exportSearchOptions);
            searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập mã phiếu, khách hàng..."); 
            searchPanel.btnReset.setActionCommand("reset");
            searchPanel.btnReset.addActionListener(this);
            menuBar.add(searchPanel, BorderLayout.SOUTH);
            searchPanel.txtSearchForm.addKeyListener(this);
        }

        this.add(menuBar, BorderLayout.NORTH);
        
        JPanel tablePanel = createExportTablePanel();
        JPanel filterPanel = createFilterPanel();
        this.add(filterPanel, BorderLayout.WEST);
        this.add(tablePanel, BorderLayout.CENTER);
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,5,0));
        panel.setPreferredSize(new Dimension(250,0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 10, 250, 10));

        List<CustomerDTO> customerList = customerBLL.getCustomerList();
        List<UserDTO> userList = userBLL.getUserList();

        JPanel customerPn = new JPanel();
        customerPn.setLayout(new GridLayout(2,1));
        customerPn.setBackground(Color.WHITE);
        JLabel customerLbl = new JLabel("Khách hàng:");
        customerCbx = new JComboBox<>();
        customerCbx.addItem("Tất cả");
        for (CustomerDTO customer : customerList) {
            customerCbx.addItem(customer.getFullName());
        }
        customerPn.add(customerLbl);
        customerPn.add(customerCbx);

        JPanel userPn = new JPanel();
        userPn.setLayout(new GridLayout(2,1));
        userPn.setBackground(Color.WHITE);
        JLabel userLbl = new JLabel("Nhân viên xuất:");
        userCbx = new JComboBox<>();
        userCbx.addItem("Tất cả");
        for (UserDTO user : userList) {
            userCbx.addItem(user.getUsername());
        }
        userPn.add(userLbl);
        userPn.add(userCbx);
        userCbx.addItemListener(this); 
        customerCbx.addItemListener(this);

        JPanel fromDatePn = new JPanel();
        fromDatePn.setLayout(new GridLayout(2,1));
        fromDatePn.setBackground(Color.WHITE);
        JLabel fromDateLbl = new JLabel("Từ ngày:");
        fromDateChooser = new JDateChooser();
        fromDateChooser.getDateEditor().getUiComponent().setFocusable(false);
        fromDatePn.add(fromDateLbl);
        fromDatePn.add(fromDateChooser);
        
        JPanel toDatePn = new JPanel();
        toDatePn.setLayout(new GridLayout(2,1));
        toDatePn.setBackground(Color.WHITE);
        JLabel toDateLbl = new JLabel("Đến ngày:");
        toDateChooser = new JDateChooser();
        toDateChooser.getDateEditor().getUiComponent().setFocusable(false);
        toDatePn.add(toDateLbl);
        toDatePn.add(toDateChooser);
        
        fromDateChooser.addPropertyChangeListener(this);
        toDateChooser.addPropertyChangeListener(this);

        panel.add(customerPn);
        panel.add(userPn);
        panel.add(fromDatePn);
        panel.add(toDatePn);

        return panel;
    }

    private JPanel createExportTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        String[] columns = {"Mã phiếu", "Khách hàng", "Ngày xuất", "Nhân viên xuất", "Tổng tiền"};
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        // table.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        // table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    public void loadDataTable(ArrayList<ExportReceiptDTO> exportData) {
        tableModel.setRowCount(0);
        for (ExportReceiptDTO exp : exportData) {
            String formattedDate = exp.getExport_date() != null ? exp.getExport_date().format(DATE_FORMATTER) : "";
            String formattedPrice = CURRENCY_FORMATTER.format(exp.getTotal_price());
            
            Object[] rowData = {
                exp.getExport_id(),
                customerBLL.getCustomerNameById(exp.getCustomer_id()),
                formattedDate,
                userBLL.getUserNameById(exp.getUser_id()),
                formattedPrice
            };
            tableModel.addRow(rowData);
        }
    }

    public int getRowSelected() {
        int index = table.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu xuất");
            return -1;
        }
        return index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "create":
                AddExportDialog dialog = new AddExportDialog(main);
                dialog.setVisible(true);
                exportList = exportBLL.getExportList();
                loadDataTable(exportList);
                break;
            case "delete":
                int selectedRow = getRowSelected();
                if (selectedRow == -1) return;
                int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa phiếu xuất đã chọn?", "Xác nhận xóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (confirm == 0) {
                    ExportReceiptDTO selectedExport = exportList.get(selectedRow);
                    if (exportBLL.cancelExport(selectedExport.getExport_id())) {
                        JOptionPane.showMessageDialog(this, "Xóa phiếu xuất thành công.");
                        exportList = exportBLL.getExportList();
                        loadDataTable(exportList);
                    } else {
                        JOptionPane.showMessageDialog(this, "Xóa phiếu xuất thất bại");
                    }
                }
                break;
            case "detail":
                int idx = getRowSelected();
                if (idx != -1) {
                    new ReceiptDetailDialog(main, "Chi tiết phiếu xuất", exportList.get(idx));
                }
                break;
            case "export":
                exportToExcel();
            break;
            case "reset":
                resetFilterInputs();
                break;
            default:
                break;
        }
    }

    public void resetFilterInputs() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        customerCbx.setSelectedIndex(0);
        userCbx.setSelectedIndex(0);
        toDateChooser.setDate(null);
        fromDateChooser.setDate(null);
    }

    public boolean validateFilterInputs(){
        // System.out.println();
        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();
        Date currentDate = new Date();

        if (fromDate != null && fromDate.after(currentDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày hiện tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            fromDateChooser.setDate(null);
            return false;
        }
        if (toDate != null && toDate.after(currentDate)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc không được lớn hơn ngày hiện tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            toDateChooser.setDate(null);
            return false;
        }
        if (fromDate != null && toDate != null && fromDate.after(toDate))
        {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            fromDateChooser.setDate(null);
            toDateChooser.setDate(null);
            return false;
        }
        if (fromDate == null && toDate != null)
        {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void filter() {
        if (validateFilterInputs()) {
            String searchTxt = searchPanel.txtSearchForm.getText().trim();            
            int prdId = customerCbx.getSelectedIndex() == 0 ? 0 : customerBLL.getCustomerIdByIdx(customerCbx.getSelectedIndex() - 1);
            int userId = userCbx.getSelectedIndex() == 0 ? 0 : userBLL.getUserIdByIdx(userCbx.getSelectedIndex() - 1);
            int searchOpt = searchPanel.cbxChoose.getSelectedIndex();
            Date fromDate = fromDateChooser.getDate() == null ? null : fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate() == null ? null : toDateChooser.getDate();
            ArrayList<ExportReceiptDTO> filteredImports = exportBLL.searchExports(searchTxt, prdId, userId, searchOpt, fromDate, toDate);
            loadDataTable(filteredImports);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == customerCbx || e.getSource() == userCbx) {
            filter();
        }
        // Xử lý khi thay đổi filter
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        filter();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == fromDateChooser || evt.getSource() == toDateChooser) {
            filter();
        }
    }

    private void exportToExcel() {
        if (exportBLL == null || exportList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            ExcelExporter.exportJTableToExcel(table);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), 
                                        "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.bat.BLL.InventoryCheckBLL;
import com.bat.BLL.UserBLL;
import com.bat.DTO.InventoryCheckDTO;
import com.bat.DTO.UserDTO;
import com.bat.GUI.Dialog.AddCheckDialog;
import com.bat.GUI.Dialog.CheckDetailDialog;
import com.bat.GUI.Main;
import com.bat.GUI.component.IntegratedSearch;
import com.bat.GUI.component.MenuFunction;
import com.toedter.calendar.JDateChooser;

public class InventoryCheck extends JPanel implements ActionListener, ItemListener, KeyListener, PropertyChangeListener {
    UserBLL userBLL = new UserBLL();
    InventoryCheckBLL checkBLL = new InventoryCheckBLL();

    DefaultTableModel tableModel;
    JTable table;
    ArrayList<InventoryCheckDTO> checkList;
    

    IntegratedSearch searchPanel;
    MenuFunction menuFunction;
    JComboBox<String> userCbx;
    JDateChooser fromDateChooser, toDateChooser;
    Main main;

    public InventoryCheck(Main main) {
        this.main = main;
        initComponent();
        checkList = checkBLL.getCheckList();
        loadDataTable(checkList);
    }

    public void initComponent() {
        this.setLayout(new BorderLayout());
        this.setBackground(new Color(228, 238, 255));
        this.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Config cho trang quản lý phiếu kiểm kê
        String[] checkButtons = {"detail", "create", "delete", "export"};
        
        String[] checkSearchOptions = {"Tất cả", "Mã phiếu kiểm kê", "Nhân viên kiểm kê"};

        JPanel menuBar = new JPanel(new BorderLayout());
        menuBar.setBackground(new Color(228, 238, 255));
        menuBar.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Header panel với title và buttons
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Title panel bên trái
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản lý phiếu kiểm kê");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Danh sách phiếu kiểm kê hàng hóa");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        subtitleLabel.setBorder(new EmptyBorder(3, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Tạo MenuFunction instance để truy cập buttons HashMap
        menuFunction = new MenuFunction(checkButtons);
        for (String btnKey : checkButtons) {
            JButton btn = menuFunction.buttons.get(btnKey);
            btn.setActionCommand(btnKey);
            btn.addActionListener(this);
        }
        
        headerPanel.add(menuFunction, BorderLayout.EAST);
        menuBar.add(headerPanel, BorderLayout.NORTH);
        
        // Search panel ở dưới nếu có config
        if (checkSearchOptions != null) {
            searchPanel = new IntegratedSearch(checkSearchOptions);
            searchPanel.txtSearchForm.putClientProperty("JTextField.placeholderText", "Nhập nội dung tìm kiếm"); 
            searchPanel.btnReset.setActionCommand("reset");
            searchPanel.btnReset.addActionListener(this);
            menuBar.add(searchPanel, BorderLayout.SOUTH);
            searchPanel.txtSearchForm.addKeyListener(this);
        }

        this.add(menuBar, BorderLayout.NORTH);
        
        // Tạo table content cho phiếu nhập
        JPanel tablePanel = createCheckTablePanel();
        JPanel filterPanel = creatFilterPanel();
        this.add(filterPanel, BorderLayout.WEST);
        this.add(tablePanel, BorderLayout.CENTER);
    }
    
    @SuppressWarnings("unchecked")
    private JPanel creatFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,5,0));
        panel.setPreferredSize(new Dimension(250,0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 10, 250, 10));

        List<UserDTO> userList = userBLL.getUserList();

        JPanel userPn = new JPanel();
        userPn.setLayout(new GridLayout(2,1));
        userPn.setBackground(Color.WHITE);
        JLabel userLbl = new JLabel("Nhân viên nhập:");
        userCbx = new JComboBox<>();
        userCbx.addItem("Tất cả");
        for (UserDTO user : userList) {
            userCbx.addItem(user.getUsername());
        }
        userPn.add(userLbl);
        userPn.add(userCbx);
        userCbx.addItemListener(this); 

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


        panel.add(userPn);
        panel.add(fromDatePn);
        panel.add(toDatePn);

        return panel;
    }

    private JPanel createCheckTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(228, 238, 255));
        panel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        // Tạo table với dữ liệu mẫu phiếu nhập
        String[] columns = {"Mã phiếu", "Nhân viên kiểm kê", "Ngày kiểm kê" };
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        // Styling table
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(229, 231, 235));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(73, 80, 87));
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(new EmptyBorder(12, 0, 12, 0));
        
        // Center align for some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    public void loadDataTable(ArrayList<InventoryCheckDTO> checkData) {
        tableModel.setRowCount(0);
        for (InventoryCheckDTO check : checkData) {
            Object[] rowData = {
                check.getCheckId(),
                userBLL.getUserNameById(check.getUserId()),
                check.getCheckDate(),
            };
            tableModel.addRow(rowData);
        }
    }

    public int getRowSelected() {
        int index = table.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu kiểm kê");
        }
        return index;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "create":
                AddCheckDialog dialog = new AddCheckDialog(main);
                dialog.setVisible(true);
                checkList = checkBLL.getCheckList();
                loadDataTable(checkList);
                break;
            case "delete":
                // System.out.println("Delete button clicked");
                int selectedRow = getRowSelected();
                int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa phiếu kiểm kê đã chọn?", "Xác nhận xóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (confirm == 0) {
                    InventoryCheckDTO selectedCheck = checkList.get(selectedRow);
                    if (checkBLL.cancelCheck(selectedCheck.getCheckId())) {
                        checkList = checkBLL.getCheckList();
                        JOptionPane.showMessageDialog(null, "Xóa phiếu kiểm kê thành công.");
                        loadDataTable(checkList);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Sản phẩm trong phiếu này đã được xuất kho, không thể xóa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case "detail":
                int idx = getRowSelected();
                CheckDetailDialog detailDialog = new CheckDetailDialog(main, checkList.get(idx));
                // detailDialog.setVisible(true);
                break;
        //     case "export":
        //         System.out.println("Export button clicked");
        //         break;
            case "reset":
                System.out.println("Reset button clicked");
                resetFilterInputs();
                break;
            default:
                break;
        }
    }

    public void resetFilterInputs() {
        searchPanel.txtSearchForm.setText("");
        searchPanel.cbxChoose.setSelectedIndex(0);
        userCbx.setSelectedIndex(0);
        fromDateChooser.setDate(null);
        toDateChooser.setDate(null);
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
            int userId = userCbx.getSelectedIndex() == 0 ? 0 : userBLL.getUserIdByIdx(userCbx.getSelectedIndex() - 1);
            int searchOpt = searchPanel.cbxChoose.getSelectedIndex();
            Date fromDate = fromDateChooser.getDate() == null ? null : fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate() == null ? null : toDateChooser.getDate();
            ArrayList<InventoryCheckDTO> filteredImports = checkBLL.searchImports(searchTxt, userId, searchOpt, fromDate, toDate);
            loadDataTable(filteredImports);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent ie) {
        if (ie.getSource() == userCbx) {
            filter();
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        // throw new UnsupportedOperationException("Not supported yet.");
        // System.out.println("Key typed");
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        // throw new UnsupportedOperationException("Not supported yet.");
        // System.out.println("Key pressed");
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        // throw new UnsupportedOperationException("Not supported yet.");
        filter();
    }

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getSource() == fromDateChooser || pce.getSource() == toDateChooser) {
            filter();
        }
    }
}
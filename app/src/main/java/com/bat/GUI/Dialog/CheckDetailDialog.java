package com.bat.GUI.Dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.bat.BLL.InventoryCheckBLL;
import com.bat.BLL.LotBLL;
import com.bat.BLL.ProductBLL;
import com.bat.BLL.UserBLL;
import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.InventoryCheckDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;

public class CheckDetailDialog extends JDialog implements ActionListener {
    private InventoryCheckDTO checkDTO;
    private ArrayList<CheckDetailDTO> detailList;

    
    private LotBLL lotBLL = new LotBLL();
    private InventoryCheckBLL checkBLL = new InventoryCheckBLL();
    private UserBLL userBLL = new UserBLL();
    private ProductBLL productBLL = new ProductBLL();

    private JPanel mainPn, main_topPn, main_btnPn, main_bPn;
    private JTextField txtImportId, txtUser, txtCreatedDate;
    private JTable table;
    private JScrollPane scrollTbl;
    private DefaultTableModel tblMode;
    private JButton btnClose, btnPDF;


    public CheckDetailDialog(JFrame main, InventoryCheckDTO checkDTO) { 
        super(main, "Chi tiết phiếu kiểm kho", true);
        this.checkDTO = checkDTO;
        this.detailList = checkBLL.getCheckDetails(checkDTO.getCheckId());
        initComponent("Chi tiết phiếu kiểm kho");
        initCheck();
        this.setVisible(true); 
    }

    public void initComponent(String title) {
        this.setTitle(title);
        this.setLayout(new BorderLayout(0,0));
        this.setSize(800, 600);

        JPanel headingPn = new JPanel();
        headingPn.setLayout(new BorderLayout());
        headingPn.setBackground(new Color(22, 122, 198));
        headingPn.setPreferredSize(new Dimension(this.getWidth(), 60));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        lblTitle.setPreferredSize(new Dimension(0,50));

        headingPn.add(lblTitle, BorderLayout.CENTER);

        mainPn = new JPanel(new BorderLayout());
        mainPn.setBorder(new EmptyBorder(5,5,5,5));
        mainPn.setBackground(Color.WHITE);
        
        main_topPn = new JPanel(new GridLayout(1,3, 10, 10));
        main_topPn.setBackground(Color.WHITE);

        JPanel impPn = new JPanel();
        impPn.setLayout(new GridLayout(2,1));
        impPn.setBackground(Color.WHITE);
        JLabel impLbl = new JLabel("Mã phiếu: ");
        txtImportId = new JTextField();   
        txtImportId.setPreferredSize(new Dimension(0,40));
        impPn.add(impLbl);
        impPn.add(txtImportId);
        
        JPanel userPn = new JPanel();
        userPn.setLayout(new GridLayout(2,1));
        userPn.setBackground(Color.WHITE);
        JLabel userLbl = new JLabel("Người tạo: ");
        txtUser = new JTextField();   
        userPn.add(userLbl);
        userPn.add(txtUser);
        
        JPanel datePn = new JPanel();
        datePn.setLayout(new GridLayout(2,1));
        datePn.setBackground(Color.WHITE);
        JLabel dateLbl = new JLabel("Ngày tạo: ");
        txtCreatedDate = new JTextField();   
        datePn.add(dateLbl);
        datePn.add(txtCreatedDate);

        txtCreatedDate.setEnabled(false);
        txtImportId.setEnabled(false);
        txtUser.setEnabled(false);

        main_topPn.add(impPn);
        main_topPn.add(userPn);
        main_topPn.add(datePn);

        main_bPn = new JPanel(new BorderLayout());
        main_bPn.setBackground(Color.WHITE);
        main_bPn.setBorder(new EmptyBorder(5,5,5,5));

        table = new JTable();
        scrollTbl = new JScrollPane();
        String[] cols = new String[] {"STT", "Mã SP", "Tên sản phẩm", "Mã lô TT", "Số lượng HT", "Số lượng TT"};
        tblMode = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tblMode);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        scrollTbl.setViewportView(table);
        
        main_bPn.add(scrollTbl);

        main_btnPn = new JPanel(new FlowLayout());
        main_btnPn.setBorder(new EmptyBorder(10,0,10,0));
        main_btnPn.setBackground(Color.WHITE);

        btnPDF = new JButton("Xuất PDF");
        btnPDF.setPreferredSize(new Dimension(100, 40));
        btnPDF.setBackground(new Color(22, 122, 198));
        btnPDF.setForeground(Color.WHITE);
        btnPDF.addActionListener(this);

        btnClose = new JButton("Đóng");
        btnClose.setPreferredSize(new Dimension(100, 40));
        btnClose.setBackground(Color.red);
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(this);

        main_btnPn.add(btnPDF);
        main_btnPn.add(btnClose);

        mainPn.add(main_topPn, BorderLayout.NORTH);
        mainPn.add(main_bPn, BorderLayout.CENTER);
        mainPn.add(main_btnPn, BorderLayout.SOUTH);


        this.add(mainPn, BorderLayout.CENTER);
        this.add(headingPn, BorderLayout.NORTH);
        this.setLocationRelativeTo(null);
    }

    public void initCheck(){
        txtImportId.setText(String.valueOf(checkDTO.getCheckId()));
        txtUser.setText(userBLL.getUserNameById(checkDTO.getUserId()));
        txtCreatedDate.setText(checkDTO.getCheckDate().toString());
        LoadCheckDetailData();
    }

    public void LoadCheckDetailData() {
        tblMode.setRowCount(0);
        for (CheckDetailDTO check : detailList) {
            ProductDTO product = productBLL.getProductByLotId(check.getLotId());
            LotDTO lot = lotBLL.getLotById(check.getLotId());
            Object[] rowData = new Object[] {
                tblMode.getRowCount() + 1,
                product.getProductId(),
                product.getProductName(),
                lot.getLotCode(),
                check.getSystemQuantity(),
                check.getActualQuantity()
            };
            tblMode.addRow(rowData);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }
}

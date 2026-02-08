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

import com.bat.BLL.LotBLL;
import com.bat.BLL.ProviderBLL;
import com.bat.BLL.UserBLL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;

public class ReceiptDetailDialog extends JDialog implements ActionListener {
    private ImportDTO importDTO;
    private ArrayList<ProductDTO> prList;

    private LotBLL lotBLL = new LotBLL();
    private UserBLL userBLL = new UserBLL();
    private ProviderBLL prdBLL = new ProviderBLL();

    private JPanel mainPn, main_topPn, main_blPn, main_brPn, main_btnPn, main_bPn;
    private JTextField txtImportId, txtUser, txtProvider, txtCreatedDate;
    private JTable table, tableLots;
    private JScrollPane scrollTbl, scrollTblLots;
    private DefaultTableModel tblMode, tblLotsModel;
    private JButton btnClose, btnPDF;


    public ReceiptDetailDialog(JFrame main, String title, ImportDTO importDTO) {
        super(main, title, true);
        this.importDTO = importDTO;
        this.prList = lotBLL.getPrdInImport(importDTO.getReceiptId());
        initComponent(title, "import");
        initImport();
        this.setVisible(true); 
    }

    public void initComponent(String title, String type) {
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
        
        main_topPn = new JPanel(new GridLayout(1,4, 10, 10));
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
        
        JPanel prdPn = new JPanel();
        prdPn.setLayout(new GridLayout(2,1));
        prdPn.setBackground(Color.WHITE);
        String txt = type.equals("import") ? "Nhà cung cấp: " : "Khách hàng: ";
        JLabel prdLbl =  new JLabel(txt);
        txtProvider = new JTextField();   
        prdPn.add(prdLbl);
        prdPn.add(txtProvider);

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
        txtProvider.setEnabled(false);

        main_topPn.add(impPn);
        main_topPn.add(userPn);
        main_topPn.add(prdPn);
        main_topPn.add(datePn);

        main_bPn = new JPanel(new BorderLayout());
        main_bPn.setBackground(Color.WHITE);
        main_bPn.setBorder(new EmptyBorder(5,5,5,5));

        main_blPn = new JPanel(new BorderLayout());
        table = new JTable();
        scrollTbl = new JScrollPane();
        String[] cols = new String[] {"Mã SP", "Tên sản phẩm", "Số lượng", "Thành tiền"};
        tblMode = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setModel(tblMode);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        scrollTbl.setViewportView(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            int idx = table.getSelectedRow();
            if (idx != -1) {
                LoadLotsData(lotBLL.getLotsByProductIdInImport(importDTO.getReceiptId(), prList.get(idx).getProductId()));
            } 
        });
        main_blPn.add(scrollTbl);

        main_brPn = new JPanel(new BorderLayout());
        main_brPn.setBackground(Color.WHITE);
        main_brPn.setPreferredSize(new Dimension(300,0));
        main_brPn.setBorder(new EmptyBorder(0,10,0,0));

        tableLots = new JTable();
        scrollTblLots = new JScrollPane();
        String[] lotCols = new String[] {"STT", "Mã lô", "Số lượng", "Đơn giá"};
        tblLotsModel = new DefaultTableModel(lotCols, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableLots.setModel(tblLotsModel);
        scrollTblLots.setViewportView(tableLots);
        main_brPn.add(scrollTblLots);

        main_bPn.add(main_blPn, BorderLayout.CENTER);
        main_bPn.add(main_brPn, BorderLayout.EAST);

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

    public void initImport() {
        txtImportId.setText(String.valueOf(importDTO.getReceiptId()));
        txtUser.setText(userBLL.getUserNameById(importDTO.getUserId()));
        txtProvider.setText(prdBLL.getProviderNameById(importDTO.getProviderId()));
        txtCreatedDate.setText(importDTO.getCreatedDate().toString());
        LoadImportDetailData();

    }

    public void LoadImportDetailData() {
        tblMode.setRowCount(0);
        for (ProductDTO prd : prList) {
            Object[] rowData = new Object[] {
                prd.getProductId(),
                prd.getProductName(),
                prd.getQuantity(),
                prd.getPrice()
            };
            tblMode.addRow(rowData);
        }
    }

    public void LoadLotsData(ArrayList<LotDTO> lotList) {
        tblLotsModel.setRowCount(0);
        for (LotDTO lot : lotList) {
            Object[] rowData = new Object[] {
                lot.getLotId(),
                lot.getLotCode(),
                lot.getQuantity(),
                lot.getImportPrice()
            };
            tblLotsModel.addRow(rowData);
        }
    }

    

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actionPerformed'");
    }}

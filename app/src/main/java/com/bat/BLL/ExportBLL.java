package com.bat.BLL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.bat.DAL.ExportLotDAL;
import com.bat.DAL.ExportReceiptDAL;
import com.bat.DAL.LotDAL;
import com.bat.DTO.ExportLotDTO;
import com.bat.DTO.ExportReceiptDTO;
import com.bat.DTO.LotDTO;

public class ExportBLL {

    private ExportReceiptDAL exportReceiptDAL = new ExportReceiptDAL();
    private ExportLotDAL exportLotDAL = new ExportLotDAL();
    private LotDAL lotDAL = new LotDAL();
    private CustomerBLL customerBLL = new CustomerBLL();
    private UserBLL userBLL = new UserBLL();
    
    ArrayList<ExportReceiptDTO> exportList;
    public ExportBLL() {
        exportList = exportReceiptDAL.getExports();
    }

    
    public ArrayList<ExportReceiptDTO> getExportList() {
        return exportReceiptDAL.getExports();
    }

    public ArrayList<LotDTO> getLotsByProductId(int productId) {
        return lotDAL.getLotsByProductId(productId);
    }

    public boolean addExport(ExportReceiptDTO exportReceipt, ArrayList<ExportLotDTO> exportLotList) {
        int exportId = exportReceiptDAL.add(exportReceipt);
        
        if (exportId != -1) {
            try {
                for (ExportLotDTO exportLot : exportLotList) {
                    exportLot.setExportId(exportId); 
                    boolean added = exportLotDAL.addExportLot(exportLot);
                    
                    if (!added) {
                        throw new RuntimeException("Lỗi khi thêm chi tiết lô xuất");
                    }
                }
                
                return true;
                
            } catch (Exception e) {
                e.printStackTrace();
                exportReceiptDAL.delete(exportId);
                return false;
            }
        }
        
        return false;
    }

    public ArrayList<ExportLotDTO> getExportLotsByExportId(int exportId, int productId) {
        ArrayList<ExportLotDTO> allLots = exportLotDAL.getExportLotsByExportId(exportId);
        ArrayList<ExportLotDTO> filteredLots = new ArrayList<>();
        for (ExportLotDTO lot : allLots) {
            if (lot.getProductId() == productId) {
                filteredLots.add(lot);
            }
        }
        return filteredLots;
    }

    public ArrayList<ExportLotDTO> getExportLotsByExportId(int exportId) {
        return exportLotDAL.getExportLotsByExportId(exportId);
    }

    public boolean cancelExport(int exportId) {
        if(exportReceiptDAL.delete(exportId)) {
            return true;
        }
        return false;
    }

    public ArrayList<ExportReceiptDTO> searchExports(String searchTxt, int customerId, int userId, int searchOpt, Date fromDate, Date toDate) {
        ArrayList<ExportReceiptDTO> filterExports = new ArrayList<>();

        ZoneId zone = ZoneId.systemDefault();

        for (ExportReceiptDTO export : exportList) {
            boolean matches = true;
            if (!searchTxt.isEmpty()) {
                String exportIdStr = String.valueOf(export.getExport_id());
                String customerName = customerBLL.getCustomerNameById(export.getCustomer_id());
                String userName = userBLL.getUserNameById(export.getUser_id());
                String searchLower = searchTxt.toLowerCase();

                switch (searchOpt) {
                    case 0: 
                        matches &= exportIdStr.contains(searchTxt) || customerName.toLowerCase().contains(searchLower) || userName.toLowerCase().contains(searchLower);
                        break;
                    case 1: 
                        matches &= exportIdStr.contains(searchTxt);
                        break;
                    case 2: 
                        matches &= customerName.toLowerCase().contains(searchLower);
                        break;
                    case 3: 
                        matches &= userName.toLowerCase().contains(searchLower);
                        break;
                }

            }
            if (customerId != 0) {
                matches &= (export.getCustomer_id() == customerId);
            }

            if (userId != 0) {
                matches &= (export.getUser_id() == userId);
            }

            if (fromDate != null) {
                LocalDateTime fromDateTime = LocalDateTime.ofInstant(fromDate.toInstant(), zone).with(LocalTime.MIN);
                matches &= !export.getExport_date().isBefore(fromDateTime);
            }

            if (toDate != null) {
                LocalDateTime toDateTime = LocalDateTime.ofInstant(toDate.toInstant(), zone).with(LocalTime.MAX);
                matches &= !export.getExport_date().isAfter(toDateTime);
            }

            if (matches) {
                filterExports.add(export);
            }
        }
        return filterExports;
    }

}

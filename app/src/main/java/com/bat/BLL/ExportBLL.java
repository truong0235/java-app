package com.bat.BLL;

import java.util.ArrayList;

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
    
    public ExportBLL() {
        // Constructor
    }

    /**
     * Lấy danh sách phiếu xuất
     */
    public ArrayList<ExportReceiptDTO> getExportList() {
        return exportReceiptDAL.getAll();
    }

    /**
     * Lấy danh sách lô theo sản phẩm
     */
    public ArrayList<LotDTO> getLotsByProductId(int productId) {
        return lotDAL.getLotsByProductId(productId);
    }

    /**
     * Thêm phiếu xuất mới
     * Logic cập nhật số lượng, lịch sử, trạng thái đã được xử lý trong DAL
     */
    public boolean addExport(ExportReceiptDTO exportReceipt, ArrayList<ExportLotDTO> exportLotList) {
        // Thêm phiếu xuất và lấy ID
        int exportId = exportReceiptDAL.add(exportReceipt);
        
        if (exportId != -1) {
            try {
                // Xử lý từng lô xuất
                for (ExportLotDTO exportLot : exportLotList) {
                    exportLot.setExportId(exportId); 
                
                    
                    // Thêm chi tiết lô xuất - DAL sẽ tự động:
                    // 1. Cập nhật số lượng lô và sản phẩm
                    // 2. Thêm lịch sử lot_transaction
                    // 3. Cập nhật trạng thái lô nếu hết
                    boolean added = exportLotDAL.addExportLot(exportLot);
                    
                    if (!added) {
                        throw new RuntimeException("Lỗi khi thêm chi tiết lô xuất");
                    }
                }
                
                return true;
                
            } catch (Exception e) {
                e.printStackTrace();
                // Có thể cần xóa phiếu xuất đã tạo
                exportReceiptDAL.delete(exportId);
                return false;
            }
        }
        
        return false;
    }

    // public ArrayList<ExportLotDTO> getExportLotsByExportId(int exportId) {
    //     return exportLotDAL.getExportLotsByExportId(exportId);
    // }

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
}

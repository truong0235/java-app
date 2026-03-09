package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.ExportHandleDAL;
import com.bat.DAL.ExportReceiptDAL;
import com.bat.DAL.LotDAL;
import com.bat.DTO.ExportHandleDTO;
import com.bat.DTO.ExportReceiptDTO;
import com.bat.DTO.LotDTO;

public class ExportBLL {

    private ExportReceiptDAL ExportReceiptDAL = new ExportReceiptDAL();
    private LotDAL lotDAL = new LotDAL();
    public ExportBLL() {
        // Constructor
    }

    public ArrayList<ExportReceiptDTO> getExportList() {
        ArrayList<ExportReceiptDTO> exportList = ExportReceiptDAL.getAll();
        return exportList;
    }   
    public ArrayList<ExportHandleDTO> getExportHandleList() {
        ArrayList<ExportHandleDTO> exportHandleList = ExportHandleDAL.exportTableQuery();
        return exportHandleList;
    }


    public ArrayList<LotDTO> getLotsByProductId(int productId) {
        ArrayList<LotDTO> lots = lotDAL.getLotsByProductId(productId);
        return lots;   
    }
}

package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.ExportReceiptDAL;
import com.bat.DTO.ExportReceiptDTO;

public class ExportBLL {

    private ExportReceiptDAL ExportReceiptDAL = new ExportReceiptDAL();
    public ExportBLL() {
        // Constructor
    }

    public ArrayList<ExportReceiptDTO> getExportList() {
        ArrayList<ExportReceiptDTO> exportList = ExportReceiptDAL.getAll();
        return exportList;
    }   
}

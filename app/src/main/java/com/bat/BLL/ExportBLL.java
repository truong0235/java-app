package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.ExportReceiptDAL;

public class ExportBLL {

    private ExportReceiptDAL ExportReceiptDAL = new ExportReceiptDAL();
    public ExportBLL() {
        // Constructor
    }

    public ArrayList getExportList() {
        ArrayList exportList = ExportReceiptDAL.getAll();
        return exportList;
    }   
}

package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.ImportDAL;
import com.bat.DAL.LotDAL;
import com.bat.DAL.LotTransactionDAL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.LotTransactionDTO;

public class ImportBLL {
    private final ImportDAL importDAL = new ImportDAL();
    private final LotDAL lotDAL = new LotDAL();
    private final LotTransactionDAL transDAL = new LotTransactionDAL();

    ArrayList<ImportDTO> importList;
    public ImportBLL() {
        importList = new ArrayList<>();
    }

    public ArrayList<ImportDTO> getImportList() {
        return importList;
    }

    public void LoadData() {
        importList = importDAL.getAllImports();
    }

    public int add(ImportDTO imp, ArrayList<LotDTO> lotList) {
        int result = importDAL.add(imp);
        if (result != -1) {
            for (LotDTO lot : lotList) {
                int lotResult = lotDAL.add(lot);
                LotTransactionDTO trans = new LotTransactionDTO(
                    0,
                    lotResult,
                    result,
                    lot.getInitialQuantity(),
                    lot.getInitialQuantity(),
                    lot.getImportDate(),
                    "import"
                );
                int trResult = transDAL.add(trans); 
                if (lotResult == -1 || trResult == -1) {
                    return -1;
                }
            }
        }
        return -1;
    }

    public boolean delete(int importId) { // xoá import, các lot liên quan, lịch sử nhập kho và kiểm tra trước khi xoá
        boolean isChecked = importDAL.checkUsedLot(importId)   ;
        if (!isChecked) {
            importDAL.delete(importId);
            lotDAL.deleteByImpId(importId);
            lotDAL.delete(importId);
            return true;
        }
        return false;
    }

    

}

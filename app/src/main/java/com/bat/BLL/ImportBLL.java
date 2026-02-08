package com.bat.BLL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.bat.DAL.ImportDAL;
import com.bat.DAL.LotDAL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;

public class ImportBLL {
    private final ImportDAL importDAL = new ImportDAL();
    private final LotDAL lotDAL = new LotDAL();
    private final ProviderBLL prdBLL = new ProviderBLL();
    private final UserBLL userBLL = new UserBLL();
    // private final LotTransactionDAL transDAL = new LotTransactionDAL();

    ArrayList<ImportDTO> importList;
    public ImportBLL() {
        importList = importDAL.getImports();
    }

    public ArrayList<ImportDTO> getImportList() {
        return importDAL.getImports();
    }

    public void LoadData() {
        importList = importDAL.getImports();
    }

    public boolean addImport(ImportDTO imp, ArrayList<LotDTO> lotList) {
        int result = importDAL.add(imp);
        if (result != -1) {
            for (LotDTO lot : lotList) {
                lot.setImportId(result);
                lotDAL.add(lot);
            }
        }
        return result != -1;
    }


    public boolean cancelImport(int importId) { // xoá import, các lot liên quan, lịch sử nhập kho và kiểm tra trước khi xoá
        boolean isChecked = importDAL.checkUsedLot(importId);
        if (isChecked) {
            importDAL.delete(importId);
            return true;
        }
        return false;
    }

    public ArrayList<ImportDTO> searchImports(String searchTxt, int providerId, int userId, int searchOpt, Date fromDate, Date toDate) {
        ArrayList<ImportDTO> filteredImports = new ArrayList<>();

        ZoneId zone = ZoneId.systemDefault();
    
        for (ImportDTO imp : importList) {
            boolean matches = true;
            // Filter by search text
            if (!searchTxt.isEmpty()) {
                String impIdStr = String.valueOf(imp.getReceiptId());
                String providerName = prdBLL.getProviderNameById(imp.getProviderId()).toLowerCase();
                String userName = userBLL.getUserNameById(imp.getUserId()).toLowerCase();
                String searchLower = searchTxt.toLowerCase();

                switch (searchOpt) {
                    case 0: // All
                        matches &= impIdStr.contains(searchTxt) || providerName.contains(searchLower) || userName.contains(searchLower);
                        break;
                    case 1: // Import ID
                        matches &= impIdStr.contains(searchTxt);
                        break;
                    case 2: // Provider Name
                        matches &= providerName.contains(searchLower);
                        break;
                    case 3: // User Name
                        matches &= userName.contains(searchLower);
                        break;
                }
            }

            // Filter by provider ID
            if (providerId != 0) {
                matches &= (imp.getProviderId() == providerId);
            }

            // Filter by user ID
            if (userId != 0) {
                matches &= (imp.getUserId() == userId);
            }

            // Filter by date range
            if (fromDate != null) {
                LocalDateTime fromDateTime = LocalDateTime.ofInstant(fromDate.toInstant(), zone).with(LocalTime.MIN);
                matches &= !imp.getCreatedDate().isBefore(fromDateTime);
            }
            if (toDate != null) {
                LocalDateTime toDateTime = LocalDateTime.ofInstant(toDate.toInstant(), zone).with(LocalTime.MAX);
                matches &= !imp.getCreatedDate().isAfter(toDateTime);
            }

            if (matches) {
                filteredImports.add(imp);
            }
        }
        return filteredImports;
    }
    

}

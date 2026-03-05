package com.bat.BLL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.bat.DAL.LotDAL;
import com.bat.DAL.LotTransactionDAL;
import com.bat.DTO.ImportDTO;
import com.bat.DTO.LotDTO;
import com.bat.DTO.LotTransactionDTO;

public class LotBLL {
    private LotDAL lotDAL = new LotDAL();
    private ProviderBLL providerBLL = new ProviderBLL();
    private UserBLL userBLL = new UserBLL();
    private ImportBLL importBLL = new ImportBLL();
    private ProductBLL productBLL = new ProductBLL();
    public LotBLL() {
    }

    public ArrayList<LotDTO> getLotList() {
        ArrayList<LotDTO> lotList = lotDAL.getLots();
        return lotDAL.getLots();
    }

    public boolean isLotCodeExist(String lotCode) {
        return lotDAL.isLotCodeExists(lotCode);
    }

    public LotDTO getLotById(int lotId) {
        return lotDAL.getLotById(lotId);
    }

    public ArrayList<LotDTO> getLotsByProductId(int productId) {
        return lotDAL.getLotsByProductId(productId);
    }

    public ArrayList<LotDTO> getLotsByImportId(int importId) {
        return lotDAL.getLotsByImpId(importId);
    }

    public ArrayList<LotDTO> getLotsByProductIdInImport(int importId, int productId) {
        ArrayList<LotDTO> allLots = lotDAL.getLotsByImpId(importId);
        ArrayList<LotDTO> filteredLots = new ArrayList<>();
        for (LotDTO lot : allLots) {
            if (lot.getProductId() == productId) {
                filteredLots.add(lot);
            }
        }
        return filteredLots;
    }

    public ArrayList<LotDTO> searchLots(String searchTxt, int providerId, int productId, int searchOpt, Date fromDate, Date toDate) {
        ArrayList<LotDTO> filteredLots = new ArrayList<>();
        ArrayList<LotDTO> lotList = lotDAL.getLots();

        ZoneId zone = ZoneId.systemDefault();

        for (LotDTO lot : lotList) {
            boolean matches = true;
            ImportDTO imp = importBLL.getImportById(lot.getImportId());
            // ProviderDTO provider = providerBLL.getProviderById(imp.getProviderId());
            if (!searchTxt.isEmpty()) {
                String searchLower = searchTxt.toLowerCase();
                String lotCode = lot.getLotCode().toLowerCase();
                String lotId = String.valueOf(lot.getLotId());
                String prdName = productBLL.getProductById(lot.getProductId()).getProductName().toLowerCase();
                switch (searchOpt) {
                    case 0:
                        matches &= lotId.contains(searchLower) || lotCode.contains(searchLower) || prdName.contains(searchLower);
                        break;
                    case 1:
                        matches &= lotId.contains(searchLower);
                        break;  
                    case 2:
                        matches &= lotCode.contains(searchLower);
                        break;
                    case 3:
                        matches &= prdName.contains(searchLower);
                        break;
                }
            }
            if (providerId != 0) {
                matches &= (imp.getProviderId() == providerId);
            }
            if (productId != 0) {
                matches &= (lot.getProductId() == productId);
            }
            if(fromDate != null) {
                LocalDateTime fromeDT = LocalDateTime.ofInstant(fromDate.toInstant(), zone).with(LocalTime.MIN);
                matches &= !imp.getCreatedDate().isBefore(fromeDT);
            }
            if(toDate != null) {
                LocalDateTime toDT = LocalDateTime.ofInstant(toDate.toInstant(), zone).with(LocalTime.MAX);
                matches &= !imp.getCreatedDate().isAfter(toDT);
            }
            if (matches) {
                filteredLots.add(lot);
            }
        }
        return filteredLots;
    }

    public ArrayList<LotTransactionDTO> getLotTransactions(int lotId) {
        LotTransactionDAL transDAL = new LotTransactionDAL();
        return transDAL.getLotTransactionsByLotId(lotId);
    }

    public boolean updateLotStatus(int lotId, String status) {
        return lotDAL.updateStatus(lotId, status);
    }
}

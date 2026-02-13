package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.LotDAL;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;

public class LotBLL {
    private LotDAL lotDAL = new LotDAL();
    public LotBLL() {
    }

    public ArrayList<LotDTO> getLots() {
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

    public ArrayList<ProductDTO> getPrdInImport(int importId) {
        return lotDAL.getPrInImport(importId);
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
}

package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.LotDAL;
import com.bat.DTO.LotDTO;
import com.bat.DTO.ProductDTO;

public class LotBLL {
    private LotDAL lotDAL = new LotDAL();
    public LotBLL() {
    }

    public boolean isLotCodeExist(String lotCode) {
        return lotDAL.isLotCodeExists(lotCode);
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

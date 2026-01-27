package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.CheckDetailDAL;
import com.bat.DAL.InventoryCheckDAL;
import com.bat.DAL.LotDAL;
import com.bat.DAL.LotTransactionDAL;
import com.bat.DAL.ProductDAL;
import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.InventoryCheckDTO;
import com.bat.DTO.LotTransactionDTO;

public class InventoryCheckBLL {
    private CheckDetailDAL checkDetailDAL;
    private InventoryCheckDAL inventoryCheckDAL;
    private LotTransactionDAL lotTransactionDAL;
    private ProductDAL productDAL;
    private LotDAL lotDAL;

    private ArrayList<InventoryCheckDTO> checkList;

    public InventoryCheckBLL() {
        inventoryCheckDAL = new InventoryCheckDAL();
        checkDetailDAL = new CheckDetailDAL();
        lotTransactionDAL = new LotTransactionDAL();
        productDAL = new ProductDAL();
        lotDAL = new LotDAL();
        checkList = new ArrayList<>();
        LoadData();
    }

    public ArrayList<InventoryCheckDTO> getCheckList() {
        return checkList;
    }

    public void setCheckList(ArrayList<InventoryCheckDTO> checkList) {
        this.checkList = checkList;
    }

    public ArrayList<CheckDetailDTO> getCheckDetails(int checkId) {
        return checkDetailDAL.getAllCheckDetails(checkId);
    }

    public void LoadData() {
        checkList = inventoryCheckDAL.getAllInventoryChecks();
    }

    public int addCheck(InventoryCheckDTO check, ArrayList<CheckDetailDTO> dList) {
        int result = inventoryCheckDAL.add(check); 
        if (result != -1) {
            for (CheckDetailDTO detail : dList) {
                detail.setCheckId(result); 
                int detailResult = checkDetailDAL.add(detail);
                LotTransactionDTO lt= new LotTransactionDTO(
                    0,
                    detail.getLotId(),
                    result,
                    detail.getActualQuantity() - detail.getSystemQuantity(),
                    detail.getActualQuantity(),
                    java.time.LocalDateTime.now(),
                    "adjust"
                );
                int trResult = lotTransactionDAL.add(lt);
                boolean lResult = lotDAL.updateQuantity(detail.getLotId(), detail.getActualQuantity()); 
                int quantity = productDAL.getQuantityPrByLotId(detail.getLotId());
                boolean pResult = productDAL.updateQuantityByLotId(detail.getLotId(), quantity + detail.getDifference());
                if (detailResult == -1 || trResult == -1 || !lResult || !pResult) {
                    return -1; 
                }
            }
            // check.setCheckId(result);
            // checkList.add(check);
        }
        return result;
    }

    public boolean cancelCheck(int checkId) {
        boolean result = checkDetailDAL.delete(checkId);
        if (result) {
            result = inventoryCheckDAL.delete(checkId);
            lotTransactionDAL.delete(checkId);
            ArrayList<CheckDetailDTO> details = this.getCheckDetails(checkId);
            for (CheckDetailDTO detail : details) {
                int id = detail.getLotId();
                int systemQty = productDAL.getQuantityPrByLotId(id);
                productDAL.updateQuantityByLotId(id, systemQty - detail.getDifference());
            }
        }
        return result;
    }
  
}

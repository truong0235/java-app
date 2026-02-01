package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.CheckDetailDAL;
import com.bat.DAL.InventoryCheckDAL;
import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.InventoryCheckDTO;

public class InventoryCheckBLL {
    private CheckDetailDAL checkDetailDAL;
    private InventoryCheckDAL inventoryCheckDAL;

    private ArrayList<InventoryCheckDTO> checkList;

    public InventoryCheckBLL() {
        inventoryCheckDAL = new InventoryCheckDAL();
        checkDetailDAL = new CheckDetailDAL();
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
        return checkDetailDAL.getCheckDetails(checkId);
    }

    public void LoadData() {
        checkList = inventoryCheckDAL.getInventoryChecks();
    }

    public boolean addCheck(InventoryCheckDTO check, ArrayList<CheckDetailDTO> dList) {
        int result = inventoryCheckDAL.add(check); 
        if (result != -1) {
            for (CheckDetailDTO detail : dList) {
                detail.setCheckId(result); 
                checkDetailDAL.add(detail);
            }
            // check.setCheckId(result);
            // checkList.add(check);
        }
        return result != -1;
    }

    public boolean cancelCheck(int checkId) {
        return inventoryCheckDAL.delete(checkId);
    }
  
}

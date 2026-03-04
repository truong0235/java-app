package com.bat.BLL;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import com.bat.DAL.CheckDetailDAL;
import com.bat.DAL.InventoryCheckDAL;
import com.bat.DTO.CheckDetailDTO;
import com.bat.DTO.InventoryCheckDTO;

public class InventoryCheckBLL {
    private CheckDetailDAL checkDetailDAL;
    private InventoryCheckDAL inventoryCheckDAL;

    private UserBLL userBLL = new UserBLL();

    private ArrayList<InventoryCheckDTO> checkList;

    public InventoryCheckBLL() {
        inventoryCheckDAL = new InventoryCheckDAL();
        checkDetailDAL = new CheckDetailDAL();
        checkList = inventoryCheckDAL.getInventoryChecks();
    }

    public ArrayList<InventoryCheckDTO> getCheckList() {
        return inventoryCheckDAL.getInventoryChecks();
    }

    public void setCheckList(ArrayList<InventoryCheckDTO> checkList) {
        this.checkList = checkList;
    }

    public ArrayList<CheckDetailDTO> getCheckDetails(int checkId) {
        return checkDetailDAL.getCheckDetails(checkId);
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
  
    public ArrayList<InventoryCheckDTO> searchImports(String searchTxt, int userId, int searchOpt, Date fromDate, Date toDate) {
        ArrayList<InventoryCheckDTO> filteredChecks = new ArrayList<>();
        ZoneId zone = ZoneId.systemDefault();
        
        for (InventoryCheckDTO check : checkList) {
            boolean matches = true;

            if(!searchTxt.isEmpty()) {
                String userName = userBLL.getUserNameById(check.getUserId()).toLowerCase();
                String searchLower = searchTxt.toLowerCase();
                String checkIdStr = String.valueOf(check.getCheckId());

                switch (searchOpt) {
                    case 0:
                        matches &=  checkIdStr.contains(searchTxt)|| userName.contains(searchLower);
                        break;
                    case 1:
                        matches &= checkIdStr.contains(searchTxt);
                        break;
                    case 2:
                        matches &= userName.contains(searchLower);
                        break;
                    default:
                        throw new AssertionError();
                }
            }

            if (userId != 0) {
                matches &= (check.getUserId() == userId);
            }

            if (fromDate != null) {
                LocalDateTime fromDT = LocalDateTime.ofInstant(fromDate.toInstant(), zone).with(LocalTime.MIN);
                matches &= !check.getCheckDate().isBefore(fromDT);
            }

            if (toDate != null) {
                LocalDateTime toDT = LocalDateTime.ofInstant(toDate.toInstant(), zone).with(LocalTime.MAX);
                matches &= !check.getCheckDate().isAfter(toDT);
            }

            if (matches) {
                filteredChecks.add(check);
            }
        }
        return filteredChecks;
    }
}

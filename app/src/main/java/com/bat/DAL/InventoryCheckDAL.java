package com.bat.DAL;

import java.time.LocalDateTime;

public class InventoryCheckDAL {
    private int checkId;
    private LocalDateTime checkDate;
    private int userId;
    private int status;

    public InventoryCheckDAL() {
        checkId = 0;
        checkDate = LocalDateTime.now();
        userId = 0;
        status = 1;
    }

    public InventoryCheckDAL(int checkId, LocalDateTime checkDate, int userId, int status) {
        this.checkId = checkId;
        this.checkDate = checkDate;
        this.userId = userId;
        this.status = status;
    }
}

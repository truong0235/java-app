package com.bat.DTO;

import java.time.LocalDateTime;

public class InventoryCheckDTO {
    private int checkId;
    private LocalDateTime checkDate;
    private int userId;
    private int status;

    public InventoryCheckDTO() {}

    public InventoryCheckDTO(int checkId, LocalDateTime checkDate, int userId, int status) {
        this.checkId = checkId;
        this.checkDate = checkDate;
        this.userId = userId;
        this.status = status;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public LocalDateTime getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDateTime checkDate) {
        this.checkDate = checkDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

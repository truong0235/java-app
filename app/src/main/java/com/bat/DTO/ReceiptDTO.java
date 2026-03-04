package com.bat.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReceiptDTO {
    private int receiptId;
    private int userId;
    private BigDecimal totalPrice;
    private int status;
    private LocalDateTime createdDate;

    public ReceiptDTO() { }
    
    public ReceiptDTO(int receiptId, int userId, BigDecimal totalPrice, int status, LocalDateTime createdDate) {
        this.receiptId = receiptId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdDate = createdDate;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}

package com.bat.DTO;

import java.time.LocalDateTime;

public class LotTransactionDTO {
    private int transId;
    private int lotId;
    private int refId;
    private int quantityChange;
    private int quantity;
    private LocalDateTime transDate;
    private String transType;

    public LotTransactionDTO() {}
    
    public LotTransactionDTO(int transId, int lotId, int refId, int quantityChange, int quantity, LocalDateTime transDate, String transType) {
        this.transId = transId;
        this.lotId = lotId;
        this.refId = refId;
        this.quantityChange = quantityChange;
        this.quantity = quantity;
        this.transDate = transDate;
        this.transType = transType;
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public int getRefId() {
        return refId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public int getQuantity() {
        return quantity;
}

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getTransDate() {
        return transDate;
    }

    public void setTransDate(LocalDateTime transDate) {
        this.transDate = transDate;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    @Override
    public String toString() {
        return "LotTransactionDTO{" +
                "transId=" + transId +
                ", lotId=" + lotId +
                ", refId=" + refId +
                ", quantityChange=" + quantityChange +
                ", quantity=" + quantity +
                ", transDate=" + transDate +
                ", transType='" + transType + '\'' +
                '}';
    }
}

package com.bat.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LotDTO {
    private int lotId;
    private String lotCode;
    private int productId;
    private int importId;
    private int initialQuantity;
    private int quantity;
    private BigDecimal importPrice;
    private int printYear;
    private String status;
    private LocalDateTime importDate;

    public LotDTO() {}
    
    public LotDTO(int lotId, String lotCode, int productId, int importId, int initialQuantity, int quantity,
                  BigDecimal importPrice, int printYear, String status, LocalDateTime importDate) {
        this.lotId = lotId;
        this.lotCode = lotCode;
        this.productId = productId;
        this.importId = importId;
        this.initialQuantity = initialQuantity;
        this.quantity = quantity;
        this.importPrice = importPrice;
        this.printYear = printYear;
        this.status = status;
        this.importDate = importDate;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getImportPrice() {
        return importPrice;
    }

    public void setImportPrice(BigDecimal importPrice) {
        this.importPrice = importPrice;
    }

    public int getPrintYear() {
        return printYear;
    }

    public void setPrintYear(int printYear) {
        this.printYear = printYear;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getImportDate() {
        return importDate;
    }

    public void setImportDate(LocalDateTime importDate) {
        this.importDate = importDate;
    }

    @Override
    public String toString() {
        return "LotDTO{" +
                "lotId=" + lotId +
                ", lotCode='" + lotCode + '\'' +
                ", productId=" + productId +
                ", importId=" + importId +
                ", initialQuantity=" + initialQuantity +
                ", quantity=" + quantity +
                ", importPrice=" + importPrice +
                ", printYear=" + printYear +
                ", status=" + status +
                ", importDate=" + importDate +
                '}';
    }
}

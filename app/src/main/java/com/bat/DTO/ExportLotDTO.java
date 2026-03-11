package com.bat.DTO;

import java.math.BigDecimal;

public class ExportLotDTO {
    private int exportId;
    private int productId;
    private int lotId;
    private int quantity;
    private BigDecimal exportPrice;
    
    private String productName;
    private String lotCode;

    public ExportLotDTO() {}
    
    public ExportLotDTO(int exportId, int productId, int lotId, int quantity, BigDecimal exportPrice) {
        this.exportId = exportId;
        this.productId = productId;
        this.lotId = lotId;
        this.quantity = quantity;
        this.exportPrice = exportPrice;
    }

    public int getExportId() {
        return exportId;
    }

    public void setExportId(int exportId) {
        this.exportId = exportId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getExportPrice() {
        return exportPrice;
    }

    public void setExportPrice(BigDecimal exportPrice) {
        this.exportPrice = exportPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    @Override
    public String toString() {
        return "ExportLotDTO{" +
                "exportId=" + exportId +
                ", productId=" + productId +
                ", lotId=" + lotId +
                ", quantity=" + quantity +
                ", exportPrice=" + exportPrice +
                ", productName='" + productName + '\'' +
                ", lotCode='" + lotCode + '\'' +
                '}';
    }
}

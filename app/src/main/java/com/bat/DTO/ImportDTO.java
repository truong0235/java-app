package com.bat.DTO;

import java.math.BigDecimal;

public class ImportDTO extends ReceiptDTO {
    private int providerId;

    public ImportDTO() {
        super();
    }

    public ImportDTO(int receiptId, int userId, BigDecimal totalPrice, int status, java.time.LocalDateTime createdDate, int providerId) {
        super(receiptId, userId, totalPrice, status, createdDate);
        this.providerId = providerId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }
    
    @Override
    public String toString() {
        return "ImportDTO{" +
               "receiptId=" + getReceiptId() +
               ", userId=" + getUserId() +
               ", totalPrice=" + getTotalPrice() +
               ", status=" + getStatus() +
               ", createdDate=" + getCreatedDate() +
               ", providerId=" + providerId +
               '}';
    }
}

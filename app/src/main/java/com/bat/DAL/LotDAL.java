package com.bat.DAL;

import java.time.LocalDateTime;

public class LotDAL {
    private int lotId;
    private int lotCode;
    private int productId;
    private int importId;
    private int initialQuantity;
    private int quantity;
    private Float importPrice;
    private int printYear, status;
    private LocalDateTime importDate;

    public LotDAL() {
        lotId = 0;
        lotCode = 0;
        productId = 0;
        importId = 0;
        initialQuantity = 0;
        quantity = 0;
        importPrice = 0f;
        printYear = 0;
        status = 1;
        importDate = LocalDateTime.now();
    }
    public LotDAL(int lotId, int lotCode, int productId, int importId, int initialQuantity, int quantity,
                  Float importPrice, int printYear, int status, LocalDateTime importDate) {
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

}

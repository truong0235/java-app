package com.bat.DAL;

import java.time.LocalDateTime;

public class LotTransactionDAL {
    private int transId;
    private int lotId;
    private int refId;
    private int quantity_change;
    private int quantity;
    private LocalDateTime transDate;
    private String transType;

    public LotTransactionDAL() {
        transId = 0;
        lotId = 0;
        refId = 0;
        quantity_change = 0;
        quantity = 0;
        transDate = LocalDateTime.now();
        transType = "";
    }
    
    public LotTransactionDAL(int transId, int lotId, int refId, int quantity_change, int quantity, LocalDateTime transDate, String transType) {
        this.transId = transId;
        this.lotId = lotId;
        this.refId = refId;
        this.quantity_change = quantity_change;
        this.quantity = quantity;
        this.transDate = transDate;
        this.transType = transType;
    }
}
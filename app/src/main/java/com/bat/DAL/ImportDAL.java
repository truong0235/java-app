package com.bat.DAL;

import java.time.LocalDateTime;
import java.util.ArrayList;
public class ImportDAL {
    private int importId;
    private int providerId;
    private int userId;
    private int status;
    private LocalDateTime importDate;
    private Float totalPrice;
    private ArrayList<LotDAL> lotList;

    public ImportDAL() {
        importId = 0;
        providerId = 0;
        userId = 0;
        status = 1;
        importDate = LocalDateTime.now();
        totalPrice = 0f;
        lotList = new ArrayList<>();
    }

    public ImportDAL(int importId, int providerId, int userId, int status, LocalDateTime importDate, Float totalPrice, ArrayList<LotDAL> lotList) {
        this.importId = importId;
        this.providerId = providerId;
        this.userId = userId;
        this.status = status;
        this.importDate = importDate;
        this.totalPrice = totalPrice;
        this.lotList = lotList;
    }
}

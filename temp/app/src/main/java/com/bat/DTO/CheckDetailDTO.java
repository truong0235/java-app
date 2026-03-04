package com.bat.DTO;

public class CheckDetailDTO {
    private int check_id;
    private int lot_id;
    private int difference;
    private int actualQuantity;
    private int systemQuantity;

    public CheckDetailDTO() { }

    public CheckDetailDTO(int check_id, int lot_id, int difference, int actualQuantity, int systemQuantity) {
        this.check_id = check_id;
        this.lot_id = lot_id;
        this.difference = difference;
        this.actualQuantity = actualQuantity;
        this.systemQuantity = systemQuantity;
    }

    public int getCheckId() {
        return check_id;
    }

    public void setCheckId(int check_id) {
        this.check_id = check_id;
    }

    public int getLotId() {
        return lot_id;
    }

    public void setLotId(int lot_id) {
        this.lot_id = lot_id;
    }

    public int getDifference() {
        return difference;
    }

    public void setDifference(int difference) {
        this.difference = difference;
    }

    public int getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(int actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public int getSystemQuantity() {
        return systemQuantity;
    }

    public void setSystemQuantity(int systemQuantity) {
        this.systemQuantity = systemQuantity;
    }

    @Override
    public String toString() {
        return "CheckDeatilDTO{" +
                "check_id=" + check_id +
                ", lot_id=" + lot_id +
                ", difference=" + difference +
                ", actualQuantity=" + actualQuantity +
                ", systemQuantity=" + systemQuantity +
                '}';
    }
}

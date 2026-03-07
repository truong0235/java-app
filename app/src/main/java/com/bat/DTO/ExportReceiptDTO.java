package com.bat.DTO;

import java.time.LocalDateTime;

public class ExportReceiptDTO {
    private int export_id;
    private LocalDateTime export_date;
    private int status;
    private int user_id;
    private int total_price;
    private int customer_id;
    private int order_id;
    
    public ExportReceiptDTO() {
    }

    public ExportReceiptDTO(int export_id, LocalDateTime export_date, int status, int user_id, int total_price, int customer_id, int order_id) {
        this.export_id = export_id;
        this.export_date = export_date;
        this.status = status;
        this.user_id = user_id;
        this.total_price = total_price;
        this.customer_id = customer_id;
        this.order_id = order_id;
    }

    public int getExport_id() {
        return export_id;
    }

    public void setExport_id(int export_id) {
        this.export_id = export_id;
    }

    public LocalDateTime getExport_date() {
        return export_date;
    }

    public void setExport_date(LocalDateTime export_date) {
        this.export_date = export_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    
}

package com.bat.DTO;

import java.time.LocalDateTime;

public class SaleOrderDTO {
    private int order_id;
    private LocalDateTime order_date;
    private LocalDateTime expected_date;
    private int status;
    private int customer_id;
    private int user_id;

    public SaleOrderDTO() {
    }

    public SaleOrderDTO(int order_id, LocalDateTime order_date, LocalDateTime expected_date, int status, int customer_id, int user_id) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.expected_date = expected_date;
        this.status = status;
        this.customer_id = customer_id;
        this.user_id = user_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public LocalDateTime getExpected_date() {
        return expected_date;
    }

    public void setExpected_date(LocalDateTime expected_date) {
        this.expected_date = expected_date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }


    public int getUser_id() {
        return user_id;
    }


    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

}

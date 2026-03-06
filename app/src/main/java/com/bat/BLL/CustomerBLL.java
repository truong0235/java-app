package com.bat.BLL;

import java.util.ArrayList;
import com.bat.DAL.CustomerDAL;
import com.bat.DTO.CustomerDTO;

public class CustomerBLL {
    private  CustomerDAL customerDAL;
    ArrayList<CustomerDTO> customers;

    public CustomerBLL() {
        customerDAL = new CustomerDAL();
        customers = customerDAL.getCustomers();
    }

    public ArrayList<CustomerDTO> getCustomerList() {
        customers = customerDAL.getCustomers();
        return customers;
    }

    public String getCustomerNameById(int customerId) {
        if (customers == null) customers = customerDAL.getCustomers();
        for (CustomerDTO c : customers) {
            if (c.getCustomerId() == customerId) return c.getFullName();
        }
        return null;
    }

    public String add(CustomerDTO c) {
        if (c.getFullName().trim().isEmpty()) return "Tên khách hàng không được để trống!";
        if (c.getPhone().trim().isEmpty()) return "Số điện thoại không được để trống!";
        if (!c.getPhone().matches("\\d{10,11}")) return "Số điện thoại phải là 10-11 chữ số!";

        int newId = customerDAL.getAutoIncrement();
        c.setCustomerId(newId);

        if (customerDAL.add(c)) return "Thêm khách hàng thành công!";
        return "Thêm thất bại!";
    }

    public String update(CustomerDTO c) {
        if (c.getFullName().trim().isEmpty()) return "Tên khách hàng không được để trống!";
        if (!c.getPhone().matches("\\d{10,11}")) return "Số điện thoại phải là 10-11 chữ số!";

        if (customerDAL.update(c)) return "Cập nhật thành công!";
        return "Cập nhật thất bại!";
    }

    public String delete(int id) {
        if (customerDAL.delete(id)) return "Xóa thành công!";
        return "Xóa thất bại!";
    }
}

package com.bat.BLL;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import com.bat.DAL.CustomerDAL;
import com.bat.DTO.CustomerDTO;

public class CustomerBLL {
    private CustomerDAL customerDAL;
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

    private String validateCustomer(CustomerDTO c, boolean isUpdate) {
        // 1. Validate tên khách hàng
        if (c.getFullName() == null || c.getFullName().trim().isEmpty()) {
            return "Tên khách hàng không được để trống!";
        }

        // 2. Validate ngày sinh
        if (c.getBirthday() != null) {
            try {
                // Chuyển Date sang LocalDate để so sánh
                LocalDate dob = c.getBirthday().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
                
                LocalDate now = LocalDate.now();
                if (dob.isAfter(now)) {
                    return "Ngày sinh không thể lớn hơn ngày hiện tại!";
                }
                
            } catch (Exception e) {
                return "Lỗi xử lý ngày sinh: " + e.getMessage();
            }
        }

        // 3. Validate số điện thoại
        if (c.getPhone() == null || c.getPhone().trim().isEmpty()) {
            return "Số điện thoại không được để trống!";
        }
        String phone = c.getPhone().trim();
        if (!phone.matches("^(0[3|5|7|8|9]\\d{8}|02\\d{8})$")) {
            return "Số điện thoại không đúng định dạng! (VD: 0901234567)";
        }
        
        // 4. Validate số điện thoại không được trùng
        int excludeId = isUpdate ? c.getCustomerId() : 0;
        if (customerDAL.isPhoneExists(phone, excludeId)) {
            return "Số điện thoại này đã được sử dụng bởi khách hàng khác!";
        }

        // 5. Validate địa chỉ (optional)
        if (c.getAddress() != null && c.getAddress().trim().length() > 200) {
            return "Địa chỉ không được vượt quá 200 ký tự!";
        }

        return null; // Tất cả đều hợp lệ
    }

    public String add(CustomerDTO c) {
        // Validate toàn bộ thông tin
        String validationError = validateCustomer(c, false);
        if (validationError != null) {
            return validationError;
        }

        int newId = customerDAL.getAutoIncrement();
        c.setCustomerId(newId);

        if (customerDAL.add(c)) {
            return "Thêm khách hàng thành công!";
        }
        return "Thêm thất bại!";
    }

    public String update(CustomerDTO c) {
        // Validate toàn bộ thông tin
        String validationError = validateCustomer(c, true);
        if (validationError != null) {
            return validationError;
        }

        if (customerDAL.update(c)) {
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String delete(int id) {
        if (customerDAL.delete(id)) {
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }
}
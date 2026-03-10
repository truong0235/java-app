package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.ProviderDAL;
import com.bat.DTO.ProviderDTO;

public class ProviderBLL {
    private ProviderDAL providerDAL;
    ArrayList<ProviderDTO> providers;

    public ProviderBLL() {
        providerDAL = new ProviderDAL();
        providers = providerDAL.getProviders();
    }

    public ArrayList<ProviderDTO> getProviderList() {
        providers = providerDAL.getProviders();
        return providers;
    }

    public ProviderDTO getProviderById(int providerId) {
        for (ProviderDTO provider : providers) {
            if (provider.getProviderId() == providerId) {
                return provider;
            }
        }
        return null;
    }

    public String getProviderNameById(int providerId) {
        if (providers == null) providers = providerDAL.getProviders();
        for (ProviderDTO provider : providers) {
            if (provider.getProviderId() == providerId) {
                return provider.getProviderName();
            }
        }
        return null;
    }
    
    public int getPrdIdByIdx(int index) {
        if (index >= 0 && index < providers.size()) {
            return providers.get(index).getProviderId();
        }
        return -1; // or throw an exception
    }

    public int getIdxByProviderId(int providerId) {
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i).getProviderId() == providerId) {
                return i;
            }
        }
        return -1;
    }

    public String add(ProviderDTO p) {
        // Validate tên nhà cung cấp
        if (p.getProviderName() == null || p.getProviderName().trim().isEmpty()) {
            return "Tên nhà cung cấp không được để trống!";
        }
        
        // Validate số điện thoại
        if (p.getPhone() == null || p.getPhone().trim().isEmpty()) {
            return "Số điện thoại không được để trống!";
        }
        if (!p.getPhone().matches("^(0[3|5|7|8|9]\\d{8}|02\\d{8})$")) {
            return "Số điện thoại không hợp lệ!";
        }
        if (isPhoneExists(p.getPhone(), -1)) {
            return "Số điện thoại này đã được sử dụng bởi nhà cung cấp khác!";
        }
        // Validate email
        if (p.getEmail() == null || p.getEmail().trim().isEmpty()) {
            return "Email không được để trống!";
        }
        if (!p.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Email không hợp lệ!";
        }
        
        // Validate địa chỉ
        // if (p.getAddress() == null || p.getAddress().trim().isEmpty()) {
        //     return "Địa chỉ không được để trống!";
        // }

        int newId = providerDAL.getAutoIncrement();
        p.setProviderId(newId);

        if (providerDAL.add(p)) {
            return "Thêm thành công!";
        }
        return "Thêm thất bại!";
    }

    public String update(ProviderDTO p) {
        // Validate tên nhà cung cấp
        if (p.getProviderName() == null || p.getProviderName().trim().isEmpty()) {
            return "Tên nhà cung cấp không được để trống!";
        }
        
        // Validate số điện thoại
        if (p.getPhone() == null || p.getPhone().trim().isEmpty()) {
            return "Số điện thoại không được để trống!";
        }
        if (!p.getPhone().matches("^(0[3|5|7|8|9]\\d{8}|02\\d{8})$")) {
            return "Số điện thoại không hợp lệ!";
        }
        if (isPhoneExists(p.getPhone(), p.getProviderId())) {
            return "Số điện thoại này đã được sử dụng bởi nhà cung cấp khác!";
        }
        
        // Validate email
        if (p.getEmail() == null || p.getEmail().trim().isEmpty()) {
            return "Email không được để trống!";
        }
        if (!p.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return "Email không hợp lệ!";
        }
        
        // Validate địa chỉ
        if (p.getAddress() == null || p.getAddress().trim().isEmpty()) {
            return "Địa chỉ không được để trống!";
        }

        if (providerDAL.update(p)) {
            return "Cập nhật thành công!";
        }
        return "Cập nhật thất bại!";
    }

    public String delete(int id) {
        if (providerDAL.delete(id)) {
            return "Xóa thành công!";
        }
        return "Xóa thất bại!";
    }

    public boolean isPhoneExists(String phone, int id) {
        for (ProviderDTO p : providers) {
            if (p.getPhone() != null && p.getPhone().equals(phone) && p.getProviderId() != id) {
                return true;
            }
        }
        return false;
    }
}

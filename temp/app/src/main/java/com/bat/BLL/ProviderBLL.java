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

    public String add(ProviderDTO p) {
        if (p.getProviderName().trim().isEmpty()) return "Tên nhà cung cấp không được để trống!";
        if (p.getPhone().trim().isEmpty()) return "Số điện thoại không được để trống!";

        if (!p.getPhone().matches("\\d{10,11}")) return "Số điện thoại phải là 10-11 chữ số!";

        int newId = providerDAL.getAutoIncrement();
        p.setProviderId(newId);

        if (providerDAL.add(p)) {
            return "Thêm thành công!";
        }
        return "Thêm thất bại!";
    }

    public String update(ProviderDTO p) {
        if (p.getProviderName().trim().isEmpty()) return "Tên nhà cung cấp không được để trống!";

        if (!p.getPhone().matches("\\d{10,11}")) return "Số điện thoại phải là 10-11 chữ số!";

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
}

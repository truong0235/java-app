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
        return providerDAL.getProviders();
    }

    public String getProviderNameById(int providerId) {
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
}

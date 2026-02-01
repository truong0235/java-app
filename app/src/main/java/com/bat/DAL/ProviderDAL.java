package com.bat.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.bat.DTO.ProviderDTO;
import com.bat.Utils.Helper.DBConnectHelper;


public class ProviderDAL {
    public ArrayList<ProviderDTO> getProviders() {
        ArrayList<ProviderDTO> providers = new ArrayList<>();
        String query = "SELECT * FROM provider";
        try {
            DBConnectHelper db = new DBConnectHelper();
            Connection conn = db.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProviderDTO provider = new ProviderDTO(
                    rs.getInt("provider_id"),
                    rs.getString("provider_name"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getString("email")
                );
                providers.add(provider);
            }
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return providers;
    }
}

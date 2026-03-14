package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.PermDAL;
import com.bat.DTO.PermissionDTO;

public class PermBLL {
    public static boolean updatePerm(int permId, int permValue) {
        return PermDAL.updatePerm(permId, permValue);
    }

    public static ArrayList<PermissionDTO> getAllPermsById(int id) {
        ArrayList<PermissionDTO> perms = new ArrayList<>();
        perms = PermDAL.getAllPermsById(id);

        return perms;
    }
}

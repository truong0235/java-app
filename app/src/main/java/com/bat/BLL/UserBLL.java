package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.UserDAL;
import com.bat.DTO.UserDTO;

public class UserBLL {
    private UserDAL userDAL;
    ArrayList<UserDTO> users;
    
    public UserBLL() {
        userDAL = new UserDAL();
        users = userDAL.getUsers();
    }

    public ArrayList<UserDTO> getUserList() {
        return userDAL.getUsers();
    }

    public String getUserNameById(int userId) {
        for (UserDTO user : users) {
            if (user.getUserId() == userId) {
                return user.getUsername();
            }
        }
        return null;
    }
}

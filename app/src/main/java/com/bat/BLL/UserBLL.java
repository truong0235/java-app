package com.bat.BLL;

import java.util.ArrayList;

import com.bat.DAL.UserDAL;
import com.bat.DTO.UserDTO;

public class UserBLL {
    private UserDAL userDAL;
    private ArrayList<UserDTO> users;
    
    public UserBLL() {
        userDAL = new UserDAL();
        users = userDAL.getUsers();
    }

    public ArrayList<UserDTO> getUserList() {
        users = userDAL.getUsers(); // Luôn lấy dữ liệu mới nhất
        return users;
    }

    public UserDTO login(String username, String password) {
        if(username.isEmpty() || password.isEmpty()) return null;
        return userDAL.checkLogin(username, password);
    }
    
    public String addUser(UserDTO u) {
        // Kiểm tra trùng username
        for(UserDTO user : getUserList()) {
            if(user.getUsername().equalsIgnoreCase(u.getUsername())) {
                return "Tên đăng nhập đã tồn tại!";
            }
        }
        if(userDAL.addUser(u) > 0) return "Thêm thành công!";
        return "Thêm thất bại!";
    }
    
    public String updateUser(UserDTO u) {
        if(userDAL.updateUser(u) > 0) return "Cập nhật thành công!";
        return "Cập nhật thất bại!";
    }
    
    public String deleteUser(int id) {
        if(userDAL.deleteUser(id) > 0) return "Xóa thành công!";
        return "Xóa thất bại!";
    }
    
    // ==========================================
    // 🔐 XỬ LÝ LOGIC ĐỔI MẬT KHẨU (XÁC MINH 3 LỚP)
    // ==========================================
    public String resetPassword(String username, String phone, String email, String newPassword) {
        // Kiểm tra xem có ô nào bị bỏ trống không
        if (username.trim().isEmpty() || phone.trim().isEmpty() || email.trim().isEmpty() || newPassword.trim().isEmpty()) {
            return "Vui lòng điền đầy đủ thông tin!";
        }
        
        // Gọi xuống DAL để thực hiện update trong Database
        int result = userDAL.resetPassword(username, phone, email, newPassword);
        
        if (result > 0) {
            return "Khôi phục mật khẩu thành công! Bạn có thể đăng nhập bằng mật khẩu mới.";
        }
        return "Thông tin xác minh (Tên đăng nhập, SĐT hoặc Email) không chính xác!";
    }

     public String getUserNameById(int userId) {
        for (UserDTO user : users) {
            if (user.getUserId() == userId) {
                return user.getUsername();
            }
        }
        return null;
    }

    public int getUserIdByIdx(int index) {
        if (index >= 0 && index < users.size()) {
            return users.get(index).getUserId();
        }
        return -1; // or throw an exception
    }

    public boolean isUsernameExists(String username) {
        for (UserDTO user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPhoneExists(String phone, int userId) {
        for (UserDTO user : users) {
            if (user.getPhone() != null && user.getPhone().equals(phone) && user.getUserId() != userId) {
                return true;
            }
        }
        return false;
    }
}

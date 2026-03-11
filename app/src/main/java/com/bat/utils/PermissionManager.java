package com.bat.utils;

import com.bat.DTO.UserDTO;

/**
 * Quản lý phân quyền cho hệ thống
 * Role IDs:
 * 1 - Quản trị viên: Tất cả chức năng
 * 2 - Nhân viên bán hàng: Sản phẩm, Danh mục, Khách hàng, Phiếu xuất
 * 3 - Nhân viên kho: Sản phẩm, Danh mục, Nhà cung cấp, Phiếu nhập, Lô hàng, Phiếu xuất
 * 4 - Nhân viên kiểm kê: Phiếu kiểm kê
 */
public class PermissionManager {
    
    // Role IDs
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_SALES = 2;
    public static final int ROLE_WAREHOUSE = 3;
    public static final int ROLE_INVENTORY_CHECK = 4;
    
    /**
     * Kiểm tra quyền truy cập chức năng quản lý sản phẩm
     */
    public static boolean canAccessProduct(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_SALES || roleId == ROLE_WAREHOUSE;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng quản lý danh mục
     */
    public static boolean canAccessCategory(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_SALES || roleId == ROLE_WAREHOUSE;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng quản lý khách hàng
     */
    public static boolean canAccessCustomer(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_SALES;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng quản lý người dùng
     */
    public static boolean canAccessUser(UserDTO user) {
        if (user == null) return false;
        return user.getRoleId() == ROLE_ADMIN;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng quản lý nhà cung cấp
     */
    public static boolean canAccessProvider(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_WAREHOUSE;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng phiếu nhập
     */
    public static boolean canAccessImport(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_WAREHOUSE;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng lô hàng
     */
    public static boolean canAccessLot(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_WAREHOUSE;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng phiếu xuất
     */
    public static boolean canAccessExport(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_SALES || roleId == ROLE_WAREHOUSE;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng kiểm kê
     */
    public static boolean canAccessInventoryCheck(UserDTO user) {
        if (user == null) return false;
        int roleId = user.getRoleId();
        return roleId == ROLE_ADMIN || roleId == ROLE_INVENTORY_CHECK;
    }
    
    /**
     * Kiểm tra quyền truy cập chức năng thống kê
     */
    public static boolean canAccessStatistic(UserDTO user) {
        if (user == null) return false;
        return user.getRoleId() == ROLE_ADMIN;
    }
    
    /**
     * Kiểm tra có phải là quản trị viên không
     */
    public static boolean isAdmin(UserDTO user) {
        if (user == null) return false;
        return user.getRoleId() == ROLE_ADMIN;
    }
    
    /**
     * Lấy tên role theo roleId
     */
    public static String getRoleName(int roleId) {
        return switch (roleId) {
            case ROLE_ADMIN -> "Quản trị viên";
            case ROLE_SALES -> "Nhân viên bán hàng";
            case ROLE_WAREHOUSE -> "Nhân viên kho";
            case ROLE_INVENTORY_CHECK -> "Nhân viên kiểm kê";
            default -> "Chưa phân quyền";
        };
    }
    
    /**
     * Kiểm tra quyền thêm/sửa/xóa dựa trên chức năng
     */
    public static boolean canModify(UserDTO user, String feature) {
        if (user == null) return false;
        
        // Admin có quyền modify tất cả
        if (isAdmin(user)) return true;
        
        return switch (feature.toLowerCase()) {
            case "product", "category" -> canAccessProduct(user);
            case "customer" -> canAccessCustomer(user);
            case "provider" -> canAccessProvider(user);
            case "import", "lot" -> canAccessImport(user);
            case "export" -> canAccessExport(user);
            case "inventorycheck" -> canAccessInventoryCheck(user);
            default -> false;
        };
    }
}

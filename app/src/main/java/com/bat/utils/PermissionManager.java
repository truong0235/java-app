package com.bat.utils;

import java.util.Map;
import java.util.Set;

import com.bat.DTO.UserDTO;

public class PermissionManager {
    public static final int ROLE_ADMIN = 1;
    public static final int ROLE_SALES = 2;
    public static final int ROLE_WAREHOUSE = 3;
    public static final int ROLE_INVENTORY_CHECK = 4;
    
    private static final Map<String, Set<Integer>> PERMISSIONS = Map.ofEntries(
        Map.entry("product", Set.of(ROLE_ADMIN, ROLE_SALES, ROLE_WAREHOUSE)),
        Map.entry("category", Set.of(ROLE_ADMIN, ROLE_SALES, ROLE_WAREHOUSE)),
        Map.entry("customer", Set.of(ROLE_ADMIN, ROLE_SALES)),
        Map.entry("user", Set.of(ROLE_ADMIN)),
        Map.entry("provider", Set.of(ROLE_ADMIN, ROLE_WAREHOUSE)),
        Map.entry("import", Set.of(ROLE_ADMIN, ROLE_WAREHOUSE)),
        Map.entry("lot", Set.of(ROLE_ADMIN, ROLE_WAREHOUSE)),
        Map.entry("export", Set.of(ROLE_ADMIN, ROLE_SALES, ROLE_WAREHOUSE)),
        Map.entry("inventorycheck", Set.of(ROLE_ADMIN, ROLE_INVENTORY_CHECK)),
        Map.entry("statistic", Set.of(ROLE_ADMIN))
    );
    
    public static boolean canAccess(UserDTO user, String feature) {
        if (user == null) return false;
        Set<Integer> allowedRoles = PERMISSIONS.get(feature.toLowerCase());
        return allowedRoles != null && allowedRoles.contains(user.getRoleId());
    }
    
    public static boolean canAccessProduct(UserDTO user) { return canAccess(user, "product"); }
    public static boolean canAccessCategory(UserDTO user) { return canAccess(user, "category"); }
    public static boolean canAccessCustomer(UserDTO user) { return canAccess(user, "customer"); }
    public static boolean canAccessUser(UserDTO user) { return canAccess(user, "user"); }
    public static boolean canAccessProvider(UserDTO user) { return canAccess(user, "provider"); }
    public static boolean canAccessImport(UserDTO user) { return canAccess(user, "import"); }
    public static boolean canAccessLot(UserDTO user) { return canAccess(user, "lot"); }
    public static boolean canAccessExport(UserDTO user) { return canAccess(user, "export"); }
    public static boolean canAccessInventoryCheck(UserDTO user) { return canAccess(user, "inventorycheck"); }
    public static boolean canAccessStatistic(UserDTO user) { return canAccess(user, "statistic"); }
    
    public static boolean isAdmin(UserDTO user) {
        return user != null && user.getRoleId() == ROLE_ADMIN;
    }
    
    public static String getRoleName(int roleId) {
        return switch (roleId) {
            case ROLE_ADMIN -> "Quản trị viên";
            case ROLE_SALES -> "Nhân viên bán hàng";
            case ROLE_WAREHOUSE -> "Nhân viên kho";
            case ROLE_INVENTORY_CHECK -> "Nhân viên kiểm kê";
            default -> "Chưa phân quyền";
        };
    }
}

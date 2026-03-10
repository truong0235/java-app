# Hệ thống phân quyền người dùng

## Tổng quan

Hệ thống quản lý kho sách đã được tích hợp chức năng phân quyền người dùng với 4 vai trò khác nhau. Mỗi vai trò có quyền truy cập vào các chức năng cụ thể của hệ thống.

## Các vai trò (Roles)

### 1. Quản trị viên (Admin) - Role ID: 1
**Quyền truy cập**: Tất cả các chức năng trong hệ thống

Chức năng:
- ✅ Trang chủ
- ✅ Sản phẩm (thêm, sửa, xóa)
- ✅ Danh mục (thêm, sửa, xóa)
- ✅ Khách hàng (thêm, sửa, xóa)
- ✅ Người dùng (thêm, sửa, xóa, phân quyền)
- ✅ Nhà cung cấp (thêm, sửa, xóa)
- ✅ Phiếu nhập (tạo, xem, xuất PDF)
- ✅ Lô hàng (quản lý)
- ✅ Phiếu xuất (tạo, xem, xuất PDF)
- ✅ Phiếu kiểm kê (tạo, xem, xuất PDF)
- ✅ Thống kê
- ✅ Đăng xuất

### 2. Nhân viên bán hàng - Role ID: 2
**Quyền truy cập**: Quản lý sản phẩm, khách hàng và phiếu xuất

Chức năng:
- ✅ Trang chủ
- ✅ Sản phẩm (xem, thêm, sửa, xóa)
- ✅ Danh mục (xem, thêm, sửa, xóa)
- ✅ Khách hàng (xem, thêm, sửa, xóa)
- ✅ Phiếu xuất (tạo, xem, xuất PDF)
- ✅ Đăng xuất
- ❌ Người dùng
- ❌ Nhà cung cấp
- ❌ Phiếu nhập
- ❌ Lô hàng
- ❌ Phiếu kiểm kê
- ❌ Thống kê

### 3. Nhân viên kho - Role ID: 3
**Quyền truy cập**: Quản lý kho, nhập xuất hàng

Chức năng:
- ✅ Trang chủ
- ✅ Sản phẩm (xem, thêm, sửa, xóa)
- ✅ Danh mục (xem, thêm, sửa, xóa)
- ✅ Nhà cung cấp (xem, thêm, sửa, xóa)
- ✅ Phiếu nhập (tạo, xem, xuất PDF)
- ✅ Lô hàng (quản lý)
- ✅ Phiếu xuất (tạo, xem, xuất PDF)
- ✅ Đăng xuất
- ❌ Khách hàng
- ❌ Người dùng
- ❌ Phiếu kiểm kê
- ❌ Thống kê

### 4. Nhân viên kiểm kê - Role ID: 4
**Quyền truy cập**: Chỉ kiểm kê hàng hóa

Chức năng:
- ✅ Trang chủ
- ✅ Phiếu kiểm kê (tạo, xem, xuất PDF)
- ✅ Đăng xuất
- ❌ Tất cả chức năng khác

## Cấu trúc Code

### 1. PermissionManager Class
**File**: `com.bat.utils.PermissionManager`

Class tiện ích quản lý phân quyền tập trung với các phương thức:

```java
// Kiểm tra quyền truy cập
PermissionManager.canAccessProduct(user)
PermissionManager.canAccessCategory(user)
PermissionManager.canAccessCustomer(user)
PermissionManager.canAccessUser(user)
PermissionManager.canAccessProvider(user)
PermissionManager.canAccessImport(user)
PermissionManager.canAccessLot(user)
PermissionManager.canAccessExport(user)
PermissionManager.canAccessInventoryCheck(user)
PermissionManager.canAccessStatistic(user)

// Kiểm tra role
PermissionManager.isAdmin(user)
PermissionManager.getRoleName(roleId)

// Kiểm tra quyền modify
PermissionManager.canModify(user, "product")
```

### 2. MenuTaskbar Integration
**File**: `com.bat.GUI.component.MenuTaskbar`

Menu sidebar tự động ẩn/hiện các chức năng dựa trên role của user đang đăng nhập.

### 3. Main Frame
**File**: `com.bat.GUI.Main`

Lưu trữ thông tin user hiện tại và cung cấp cho các component khác qua phương thức `getCurrentUser()`.

## Sử dụng trong code

### Kiểm tra quyền trong Panel
```java
public class SomePanel extends JPanel {
    private UserDTO currentUser;
    
    public SomePanel(UserDTO user) {
        this.currentUser = user;
        initComponent();
        applyPermissions();
    }
    
    private void applyPermissions() {
        // Ẩn nút thêm nếu không có quyền modify
        if (!PermissionManager.canModify(currentUser, "product")) {
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        }
    }
}
```

### Lấy thông tin user từ Main
```java
Main mainFrame = (Main) SwingUtilities.getWindowAncestor(this);
UserDTO currentUser = mainFrame.getCurrentUser();

if (PermissionManager.isAdmin(currentUser)) {
    // Thực hiện action dành cho admin
}
```

## Testing

### Test các vai trò
1. Tạo user với các role khác nhau trong database
2. Đăng nhập với từng user
3. Kiểm tra menu sidebar chỉ hiển thị các chức năng phù hợp với role
4. Thử truy cập các chức năng và kiểm tra quyền add/edit/delete

### Test Cases
- ✅ Admin xem tất cả menu
- ✅ Nhân viên bán hàng chỉ xem: Sản phẩm, Danh mục, Khách hàng, Phiếu xuất
- ✅ Nhân viên kho chỉ xem: Sản phẩm, Danh mục, Nhà cung cấp, Phiếu nhập, Lô hàng, Phiếu xuất
- ✅ Nhân viên kiểm kê chỉ xem: Phiếu kiểm kê
- ✅ Tất cả user đều xem được: Trang chủ và Đăng xuất

## Mở rộng

### Thêm vai trò mới
1. Định nghĩa role ID mới trong `PermissionManager`
2. Thêm case trong `getRoleName()`
3. Cập nhật logic quyền truy cập trong các method `canAccess*()`
4. Update database với role mới

### Thêm quyền chi tiết hơn
Có thể tách quyền xem/thêm/sửa/xóa riêng biệt:
```java
public static boolean canAddProduct(UserDTO user) { ... }
public static boolean canEditProduct(UserDTO user) { ... }
public static boolean canDeleteProduct(UserDTO user) { ... }
```

## Lưu ý bảo mật
- Luôn kiểm tra quyền ở cả frontend (UI) và backend (BLL/DAL)
- Không tin tưởng hoàn toàn vào việc ẩn/hiện UI
- Validate quyền trước khi thực hiện các action quan trọng
- Log các action để audit khi cần

## Tài liệu liên quan
- UserDTO: Lớp đối tượng người dùng
- LoginJFrame: Xử lý đăng nhập và khởi tạo session
- Main: Frame chính của ứng dụng

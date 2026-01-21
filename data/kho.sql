-- Tạo database với chuẩn mã hóa tiếng Việt
DROP DATABASE IF EXISTS KhoSach;
CREATE DATABASE KhoSach CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE KhoSach;
SET SQL_SAFE_UPDATES = 0;

-- =======================================================
-- TẠO CẤU TRÚC BẢNG (STRUCTURE)
-- =======================================================

-- 1. Bảng Category (Danh mục sách)
CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL,
    description TEXT,
    status TINYINT DEFAULT 1 COMMENT '1: Active, 0: Inactive'
);

-- 2. Bảng Provider (Nhà cung cấp / NXB)
CREATE TABLE provider (
    provider_id INT AUTO_INCREMENT PRIMARY KEY,
    provider_name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(100)
);

-- 3. Bảng Customer (Khách hàng)
CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(255) NOT NULL,
    birthday DATE,
    phone VARCHAR(20),
    address VARCHAR(500)
);

-- 4. Bảng Functions
CREATE TABLE functions (
    function_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    icon varchar(255)
);

-- 5. Bảng Roles
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1
);

-- 6. Bảng Role Details
CREATE TABLE role_details (
    role_id INT,
    function_id INT,
    action VARCHAR(50),
    PRIMARY KEY (role_id, function_id, action),
    FOREIGN KEY (role_id) REFERENCES roles(role_id),
    FOREIGN KEY (function_id) REFERENCES functions(function_id)
);

-- 7. Bảng Users
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    role_id INT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    fullname VARCHAR(255),
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(500),
    status TINYINT DEFAULT 1,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- 8. Bảng Product (Sách)
CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    publisher VARCHAR(255),
    publish_year INT,
    author VARCHAR(255),
    language VARCHAR(50),
    price DECIMAL(18, 2),
    quantity INT DEFAULT 0,
    status INT DEFAULT 1,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES category(category_id)
);

-- 9. Bảng Import Receipt
CREATE TABLE import_receipt (
    import_id INT AUTO_INCREMENT PRIMARY KEY,
    import_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status INT,
    total_price DECIMAL(18, 2),
    provider_id INT,
    user_id INT,
    FOREIGN KEY (provider_id) REFERENCES provider(provider_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 10. Bảng Lot
CREATE TABLE lot (
    lot_id INT AUTO_INCREMENT PRIMARY KEY,
    lot_code VARCHAR(50) NOT NULL,
    import_date DATETIME,
    initial_quantity INT NOT NULL,
    quantity INT NOT NULL,
    print_year INT,
    import_price DECIMAL(18, 2),
    status INT,
    import_id INT,
    product_id INT,
    FOREIGN KEY (import_id) REFERENCES import_receipt(import_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 11. Bảng Inventory Check
CREATE TABLE inventory_check (
    check_id INT AUTO_INCREMENT PRIMARY KEY,
    check_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status INT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 12. Bảng Inventory Check Detail
CREATE TABLE inventory_check_detail (
    check_id INT,
    lot_id INT,
    actual_quantity INT,
    system_quantity INT,
    difference INT,
    PRIMARY KEY (check_id, lot_id),
    FOREIGN KEY (check_id) REFERENCES inventory_check(check_id),
    FOREIGN KEY (lot_id) REFERENCES lot(lot_id)
);

-- 13. Bảng Lot Transaction
CREATE TABLE lot_transaction (
    trans_id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50),
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    quantity_change INT,
    quantity INT,
    ref_id INT,
    lot_id INT,
    FOREIGN KEY (lot_id) REFERENCES lot(lot_id)
);

-- 14. Bảng Sales Order
CREATE TABLE sales_order (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    expected_date DATETIME,
    status INT,
    customer_id INT,
    user_id INT,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 15. Bảng Sales Order Detail
CREATE TABLE sales_order_detail (
    order_id INT,
    product_id INT,
    quantity INT,
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES sales_order(order_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 16. Bảng Export Receipt
CREATE TABLE export_receipt (
    export_id INT AUTO_INCREMENT PRIMARY KEY,
    export_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status INT,
    total_price DECIMAL(18, 2),
    user_id INT,
    customer_id INT,
    order_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    FOREIGN KEY (order_id) REFERENCES sales_order(order_id)
);

-- 17. Bảng Export Detail
CREATE TABLE export_detail (
    export_id INT,
    product_id INT,
    PRIMARY KEY (export_id, product_id),
    FOREIGN KEY (export_id) REFERENCES export_receipt(export_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);

-- 18. Bảng Export Lot
CREATE TABLE export_lot (
    export_id INT,
    product_id INT,
    lot_id INT,
    quantity INT,
    export_price DECIMAL(18, 2),
    PRIMARY KEY (export_id, lot_id),
    FOREIGN KEY (export_id) REFERENCES export_receipt(export_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (lot_id) REFERENCES lot(lot_id)
);

-- =======================================================
-- 1. MASTER DATA - DỮ LIỆU ĐÃ CHỈNH SỬA CHO KHO SÁCH
-- =======================================================

-- 1.1 Category (30 Thể loại sách)
INSERT INTO category (category_name, description, status) VALUES 
('Văn học Việt Nam', 'Tiểu thuyết, truyện ngắn VN', 1),
('Văn học Nước Ngoài', 'Tiểu thuyết dịch', 1),
('Sách Kinh Tế', 'Quản trị, Marketing, Tài chính', 1),
('Sách Kỹ Năng Sống', 'Self-help, phát triển bản thân', 1),
('Sách Thiếu Nhi', 'Truyện tranh, cổ tích, ehon', 1),
('Truyện Tranh (Manga)', 'Truyện tranh Nhật Bản', 1),
('Sách Giáo Khoa', 'Sách giáo dục các cấp', 1),
('Sách Ngoại Ngữ', 'Tiếng Anh, Nhật, Trung, Hàn', 1),
('Sách Lịch Sử', 'Lịch sử Việt Nam và thế giới', 1),
('Sách Tôn Giáo', 'Phật giáo, Thiên chúa giáo', 1),
('Sách Tâm Lý Học', 'Tâm lý hành vi, trị liệu', 1),
('Tiểu Thuyết Trinh Thám', 'Sherlock Holmes, Conan Doyle', 1),
('Tiểu Thuyết Kinh Dị', 'Stephen King, Nguyễn Ngọc Ngạn', 1),
('Tiểu Thuyết Lãng Mạn', 'Ngôn tình, Romance', 1),
('Khoa Học Viễn Tưởng', 'Sci-fi, Fantasy', 1),
('Sách Y Học', 'Dược, dinh dưỡng, giải phẫu', 1),
('Sách Nấu Ăn', 'Công thức nấu ăn, pha chế', 1),
('Sách Du Lịch', 'Cẩm nang du lịch, khám phá', 1),
('Sách Kiến Trúc', 'Thiết kế, xây dựng', 1),
('Sách Nông Nghiệp', 'Trồng trọt, chăn nuôi', 1),
('Sách Chính Trị', 'Lý luận chính trị, pháp luật', 1),
('Từ Điển', 'Từ điển các loại', 1),
('Sách Tô Màu', 'Artbook, sách giải trí', 1),
('Tạp Chí', 'Thời trang, công nghệ', 1),
('Sách Phong Thủy', 'Tử vi, tướng số', 1),
('Sách Nuôi Dạy Con', 'Phương pháp giáo dục trẻ', 1),
('Sách Tiểu Sử', 'Hồi ký nhân vật nổi tiếng', 1),
('Sách Nghệ Thuật', 'Hội họa, âm nhạc, điện ảnh', 1),
('Sách Thể Thao', 'Bóng đá, Gym, Yoga', 1),
('Sách Công Nghệ', 'Lập trình, AI, Blockchain', 1);

-- 1.2 Provider (30 Nhà Xuất Bản & Công ty sách)
INSERT INTO provider (provider_name, address, phone, email) VALUES 
('NXB Kim Đồng', 'Hà Nội', '02439434730', 'kimdong@nxb.vn'),
('NXB Trẻ', 'TP.HCM', '02839316289', 'nxbtre@tre.vn'),
('Nhã Nam', 'Hà Nội', '02435146875', 'info@nhanam.vn'),
('Alpha Books', 'Hà Nội', '0909123456', 'support@alphabooks.vn'),
('First News - Trí Việt', 'TP.HCM', '02838227979', 'tri@firstnews.com.vn'),
('Thái Hà Books', 'Hà Nội', '02437930480', 'info@thaihabooks.com'),
('Đông A', 'TP.HCM', '02838462702', 'donga@donga.vn'),
('Đinh Tị Books', 'Hà Nội', '02437192626', 'dinhti@dinhti.vn'),
('NXB Giáo Dục VN', 'Hà Nội', '02439422010', 'nxbgd@moet.gov.vn'),
('NXB Tổng Hợp TP.HCM', 'TP.HCM', '02838225340', 'tonghop@nxbhcm.vn'),
('NXB Văn Học', 'Hà Nội', '02438253689', 'nxbvanhoc@hn.vn'),
('NXB Hội Nhà Văn', 'Hà Nội', '02438253690', 'hoinhavan@vn.vn'),
('NXB Phụ Nữ', 'Hà Nội', '02438253691', 'phunu@nxb.vn'),
('NXB Lao Động', 'Hà Nội', '02438253692', 'laodong@nxb.vn'),
('NXB Thanh Niên', 'Hà Nội', '02438253693', 'thanhnien@nxb.vn'),
('NXB Thế Giới', 'Hà Nội', '02438253694', 'thegioi@nxb.vn'),
('NXB Tri Thức', 'Hà Nội', '02438253695', 'trithuc@nxb.vn'),
('Huy Hoàng Bookstore', 'TP.HCM', '02838253696', 'huyhoang@book.vn'),
('Minh Long Book', 'Hà Nội', '02437346888', 'minhlongbook@gmail.com'),
('MCBooks', 'Hà Nội', '02437921466', 'mcbooks@gmail.com'),
('1980 Books', 'Hà Nội', '02437880225', '1980books@gmail.com'),
('SkyBooks', 'Hà Nội', '02432002660', 'skybooks@gmail.com'),
('Bloom Books', 'Hà Nội', '02432002661', 'bloom@sky.com'),
('NXB Xây Dựng', 'Hà Nội', '02438253700', 'xaydung@nxb.vn'),
('NXB Y Học', 'Hà Nội', '02438253701', 'yhoc@nxb.vn'),
('NXB Chính Trị QG', 'Hà Nội', '02438253702', 'chinhtri@nxb.vn'),
('NXB Thông Tin TT', 'Hà Nội', '02438253703', 'tttt@nxb.vn'),
('NXB ĐHQG Hà Nội', 'Hà Nội', '02438253704', 'dhqghn@nxb.vn'),
('NXB ĐHQG TP.HCM', 'TP.HCM', '02838253705', 'dhqghcm@nxb.vn'),
('ZenBooks', 'TP.HCM', '02838253706', 'zenbooks@vn.vn');

-- 1.3 Customer (Giữ nguyên)
INSERT INTO customer (fullname, birthday, phone, address) VALUES 
('Nguyễn Văn A', '1990-01-01', '0901000001', 'Hà Nội'),
('Trần Thị B', '1991-02-02', '0901000002', 'TP.HCM'),
('Lê Văn C', '1992-03-03', '0901000003', 'Đà Nẵng'),
('Phạm Thị D', '1993-04-04', '0901000004', 'Cần Thơ'),
('Hoàng Văn E', '1994-05-05', '0901000005', 'Hải Phòng'),
('Vũ Thị F', '1995-06-06', '0901000006', 'Quảng Ninh'),
('Đặng Văn G', '1996-07-07', '0901000007', 'Nghệ An'),
('Bùi Thị H', '1997-08-08', '0901000008', 'Thanh Hóa'),
('Đỗ Văn I', '1998-09-09', '0901000009', 'Bắc Ninh'),
('Hồ Thị K', '1999-10-10', '0901000010', 'Huế'),
('Ngô Văn L', '2000-11-11', '0901000011', 'Khánh Hòa'),
('Dương Thị M', '2001-12-12', '0901000012', 'Lâm Đồng'),
('Lý Văn N', '1985-01-13', '0901000013', 'Bình Dương'),
('Vương Thị O', '1986-02-14', '0901000014', 'Đồng Nai'),
('Trịnh Văn P', '1987-03-15', '0901000015', 'Vũng Tàu'),
('Đinh Thị Q', '1988-04-16', '0901000016', 'Long An'),
('Lâm Văn R', '1989-05-17', '0901000017', 'Tiền Giang'),
('Mai Thị S', '1990-06-18', '0901000018', 'Bến Tre'),
('Cao Văn T', '1991-07-19', '0901000019', 'Vĩnh Long'),
('Phan Thị U', '1992-08-20', '0901000020', 'Đồng Tháp'),
('Hà Văn V', '1993-09-21', '0901000021', 'An Giang'),
('Tạ Thị X', '1994-10-22', '0901000022', 'Kiên Giang'),
('Châu Văn Y', '1995-11-23', '0901000023', 'Cà Mau'),
('Thái Thị Z', '1996-12-24', '0901000024', 'Sóc Trăng'),
('Trương Văn AA', '1997-01-25', '0901000025', 'Bạc Liêu'),
('Phùng Thị BB', '1998-02-26', '0901000026', 'Hà Nam'),
('Nghiêm Văn CC', '1999-03-27', '0901000027', 'Nam Định'),
('Bạch Thị DD', '2000-04-28', '0901000028', 'Thái Bình'),
('Khổng Văn EE', '2001-05-29', '0901000029', 'Hưng Yên'),
('Lưu Thị FF', '2002-06-30', '0901000030', 'Hải Dương');

-- 1.4 Roles & Users (Giữ nguyên)
INSERT INTO roles (name, status) VALUES 
('Admin', 1), ('Manager', 1), ('Sale', 1), ('Warehouse', 1), ('Accountant', 1);

INSERT INTO users (role_id, username, password, fullname, phone, email, status) VALUES 
(1, 'admin', 'hash_pass', 'Super Admin', '0901000001', 'admin@sys.com', 1),
(2, 'manager1', 'hash_pass', 'Nguyễn Quản Lý', '0901000002', 'manager1@sys.com', 1),
(4, 'kho1', 'hash_pass', 'Trần Thủ Kho 1', '0901000003', 'kho1@sys.com', 1),
(4, 'kho2', 'hash_pass', 'Lê Thủ Kho 2', '0901000004', 'kho2@sys.com', 1),
(4, 'kho3', 'hash_pass', 'Phạm Thủ Kho 3', '0901000005', 'kho3@sys.com', 1),
(3, 'sale1', 'hash_pass', 'Vũ Sale 1', '0901000006', 'sale1@sys.com', 1),
(3, 'sale2', 'hash_pass', 'Đặng Sale 2', '0901000007', 'sale2@sys.com', 1),
(3, 'sale3', 'hash_pass', 'Bùi Sale 3', '0901000008', 'sale3@sys.com', 1),
(3, 'sale4', 'hash_pass', 'Đỗ Sale 4', '0901000009', 'sale4@sys.com', 1),
(3, 'sale5', 'hash_pass', 'Hồ Sale 5', '0901000010', 'sale5@sys.com', 1),
(3, 'sale6', 'hash_pass', 'Ngô Sale 6', '0901000011', 'sale6@sys.com', 1),
(3, 'sale7', 'hash_pass', 'Dương Sale 7', '0901000012', 'sale7@sys.com', 1),
(3, 'sale8', 'hash_pass', 'Lý Sale 8', '0901000013', 'sale8@sys.com', 1),
(3, 'sale9', 'hash_pass', 'Vương Sale 9', '0901000014', 'sale9@sys.com', 1),
(3, 'sale10', 'hash_pass', 'Trịnh Sale 10', '0901000015', 'sale10@sys.com', 1),
(5, 'acc1', 'hash_pass', 'Đinh Kế Toán 1', '0901000016', 'acc1@sys.com', 1),
(5, 'acc2', 'hash_pass', 'Lâm Kế Toán 2', '0901000017', 'acc2@sys.com', 1),
(5, 'acc3', 'hash_pass', 'Mai Kế Toán 3', '0901000018', 'acc3@sys.com', 1),
(5, 'acc4', 'hash_pass', 'Cao Kế Toán 4', '0901000019', 'acc4@sys.com', 1),
(5, 'acc5', 'hash_pass', 'Phan Kế Toán 5', '0901000020', 'acc5@sys.com', 1),
(4, 'kho4', 'hash_pass', 'Hà Kho 4', '0901000021', 'kho4@sys.com', 1),
(4, 'kho5', 'hash_pass', 'Tạ Kho 5', '0901000022', 'kho5@sys.com', 1),
(4, 'kho6', 'hash_pass', 'Châu Kho 6', '0901000023', 'kho6@sys.com', 1),
(4, 'kho7', 'hash_pass', 'Thái Kho 7', '0901000024', 'kho7@sys.com', 1),
(4, 'kho8', 'hash_pass', 'Trương Kho 8', '0901000025', 'kho8@sys.com', 1),
(4, 'kho9', 'hash_pass', 'Phùng Kho 9', '0901000026', 'kho9@sys.com', 1),
(4, 'kho10', 'hash_pass', 'Nghiêm Kho 10', '0901000027', 'kho10@sys.com', 1),
(2, 'manager2', 'hash_pass', 'Bạch Quản Lý 2', '0901000028', 'manager2@sys.com', 1),
(2, 'manager3', 'hash_pass', 'Khổng Quản Lý 3', '0901000029', 'manager3@sys.com', 1),
(1, 'admin2', 'hash_pass', 'Lưu Admin 2', '0901000030', 'admin2@sys.com', 1);

-- =======================================================
-- 2. DỮ LIỆU SẢN PHẨM & NHẬP KHO (INBOUND)
-- =======================================================

-- 2.1 Product (30 Quyển sách, khớp với 30 Category ở trên)
INSERT INTO product (product_name, publisher, publish_year, author, language, price, quantity, category_id) VALUES
('Dế Mèn Phiêu Lưu Ký', 'Kim Đồng', 2020, 'Tô Hoài', 'Tiếng Việt', 50000, 100, 1),
('Harry Potter và Hòn Đá Phù Thủy', 'NXB Trẻ', 2021, 'J.K. Rowling', 'Tiếng Việt', 150000, 100, 2),
('Marketing Căn Bản', 'Đại Học Kinh Tế', 2022, 'Philip Kotler', 'Tiếng Việt', 250000, 100, 3),
('Đắc Nhân Tâm', 'First News', 2023, 'Dale Carnegie', 'Tiếng Việt', 86000, 100, 4),
('Doraemon Tập 1', 'Kim Đồng', 2022, 'Fujiko F', 'Tiếng Việt', 25000, 100, 5),
('Thám Tử Lừng Danh Conan 100', 'Kim Đồng', 2023, 'Gosho Aoyama', 'Tiếng Việt', 25000, 100, 6),
('Toán Lớp 1', 'GDVN', 2023, 'Bộ GD&ĐT', 'Tiếng Việt', 20000, 100, 7),
('Hack Não 1500 Từ Tiếng Anh', 'MCBooks', 2021, 'Nguyễn Hiệp', 'Anh-Việt', 190000, 100, 8),
('Đại Việt Sử Ký Toàn Thư', 'Văn Học', 2018, 'Ngô Sĩ Liên', 'Tiếng Việt', 450000, 100, 9),
('Kinh Thánh', 'Tôn Giáo', 2020, 'Nhiều TG', 'Tiếng Việt', 200000, 100, 10),
('Tâm Lý Học Đám Đông', 'Nhã Nam', 2019, 'Gustave Le Bon', 'Tiếng Việt', 120000, 100, 11),
('Sherlock Holmes Toàn Tập', 'Văn Học', 2020, 'Conan Doyle', 'Tiếng Việt', 300000, 100, 12),
('IT - Chú Hề Ma Quái', 'Nhã Nam', 2019, 'Stephen King', 'Tiếng Việt', 250000, 100, 13),
('Mắt Biếc', 'NXB Trẻ', 2019, 'Nguyễn Nhật Ánh', 'Tiếng Việt', 110000, 100, 14),
('Dune - Xứ Cát', 'Nhã Nam', 2021, 'Frank Herbert', 'Tiếng Việt', 220000, 100, 15),
('Giải Phẫu Người', 'Y Học', 2022, 'Frank Netter', 'Tiếng Việt', 800000, 100, 16),
('Nấu Ăn Ngon Mỗi Ngày', 'Phụ Nữ', 2020, 'Triệu Thị Chơi', 'Tiếng Việt', 60000, 100, 17),
('Việt Nam Tươi Đẹp', 'Du Lịch', 2019, 'Nhiều TG', 'Tiếng Việt', 90000, 100, 18),
('Lịch Sử Kiến Trúc Phương Tây', 'Xây Dựng', 2018, 'Đặng Thái Hoàng', 'Tiếng Việt', 150000, 100, 19),
('Kỹ Thuật Trồng Lan', 'Nông Nghiệp', 2020, 'Nguyễn Văn A', 'Tiếng Việt', 45000, 100, 20),
('Hiến Pháp Việt Nam', 'Chính Trị QG', 2013, 'Quốc Hội', 'Tiếng Việt', 30000, 100, 21),
('Từ Điển Oxford Anh-Việt', 'ĐHQG HN', 2021, 'Oxford', 'Anh-Việt', 350000, 100, 22),
('Khu Vườn Bí Mật (Tô Màu)', 'Nhã Nam', 2016, 'Johanna Basford', 'Tiếng Việt', 80000, 100, 23),
('Tạp Chí Forbes VN', 'Forbes', 2024, 'Forbes', 'Tiếng Việt', 50000, 100, 24),
('Tử Vi Đẩu Số', 'Văn Hóa TT', 2019, 'Vân Đằng', 'Tiếng Việt', 120000, 100, 25),
('Chờ Đến Mẫu Giáo Thì Đã Muộn', 'Thái Hà', 2018, 'Ibuka Masaru', 'Tiếng Việt', 70000, 100, 26),
('Steve Jobs - Tiểu Sử', 'Alpha Books', 2017, 'Walter Isaacson', 'Tiếng Việt', 200000, 100, 27),
('Câu Chuyện Nghệ Thuật', 'Dân Trí', 2019, 'E.H. Gombrich', 'Tiếng Việt', 500000, 100, 28),
('Hồi Ký Alex Ferguson', 'NXB Trẻ', 2015, 'Alex Ferguson', 'Tiếng Việt', 130000, 100, 29),
('Clean Code - Mã Sạch', 'Thanh Niên', 2020, 'Robert C. Martin', 'Tiếng Việt', 300000, 100, 30);

-- 2.2 Import Receipt (30 phiếu nhập)
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) VALUES
('2024-01-01', 1, 3000000, 1, 3), ('2024-01-02', 1, 5000000, 2, 3), ('2024-01-03', 1, 1000000, 3, 3), 
('2024-01-04', 1, 1000000, 4, 3), ('2024-01-05', 1, 10000000, 5, 3), ('2024-01-06', 1, 4000000, 6, 3),
('2024-02-01', 1, 8000000, 7, 4), ('2024-02-02', 1, 20000000, 8, 4), ('2024-02-03', 1, 40000000, 9, 4), 
('2024-02-04', 1, 15000000, 10, 4), ('2024-02-05', 1, 300000, 11, 4), ('2024-02-06', 1, 200000, 12, 4),
('2024-03-01', 1, 800000, 13, 5), ('2024-03-02', 1, 600000, 14, 5), ('2024-03-03', 1, 1000000, 15, 5), 
('2024-03-04', 1, 3000000, 16, 5), ('2024-03-05', 1, 5000000, 15, 5), ('2024-03-06', 1, 3000000, 1, 5),
('2024-04-01', 1, 2000000, 2, 3), ('2024-04-02', 1, 800000, 3, 3), ('2024-04-03', 1, 50000000, 16, 3), 
('2024-04-04', 1, 35000000, 17, 3), ('2024-04-05', 1, 2500000, 18, 3), ('2024-04-06', 1, 6000000, 19, 3),
('2024-05-01', 1, 1500000, 11, 4), ('2024-05-02', 1, 800000, 12, 4), ('2024-05-03', 1, 400000, 11, 4), 
('2024-05-04', 1, 3000000, 1, 4), ('2024-05-05', 1, 1000000, 12, 4), ('2024-05-06', 1, 500000, 13, 4);

-- 2.3 Lot (30 lô hàng)
INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id) VALUES
('L01', '2024-01-01', 100, 100, 2023, 35000, 1, 1, 1),
('L02', '2024-01-02', 100, 100, 2023, 105000, 1, 2, 2),
('L03', '2024-01-03', 100, 100, 2023, 175000, 1, 3, 3),
('L04', '2024-01-04', 100, 100, 2023, 60000, 1, 4, 4),
('L05', '2024-01-05', 100, 100, 2023, 17500, 1, 5, 5),
('L06', '2024-01-06', 100, 100, 2023, 17500, 1, 6, 6),
('L07', '2024-02-01', 100, 100, 2023, 14000, 1, 7, 7),
('L08', '2024-02-02', 100, 100, 2023, 133000, 1, 8, 8),
('L09', '2024-02-03', 100, 100, 2023, 315000, 1, 9, 9),
('L10', '2024-02-04', 100, 100, 2023, 140000, 1, 10, 10),
('L11', '2024-02-05', 100, 100, 2024, 84000, 1, 11, 11),
('L12', '2024-02-06', 100, 100, 2024, 210000, 1, 12, 12),
('L13', '2024-03-01', 100, 100, 2024, 175000, 1, 13, 13),
('L14', '2024-03-02', 100, 100, 2024, 77000, 1, 14, 14),
('L15', '2024-03-03', 100, 100, 2024, 154000, 1, 15, 15),
('L16', '2024-03-04', 100, 100, 2024, 560000, 1, 16, 16),
('L17', '2024-03-05', 100, 100, 2024, 42000, 1, 17, 17),
('L18', '2024-03-06', 100, 100, 2024, 63000, 1, 18, 18),
('L19', '2024-04-01', 100, 100, 2024, 105000, 1, 19, 19),
('L20', '2024-04-02', 100, 100, 2024, 31500, 1, 20, 20),
('L21', '2024-04-03', 100, 100, 2023, 21000, 1, 21, 21),
('L22', '2024-04-04', 100, 100, 2023, 245000, 1, 22, 22),
('L23', '2024-04-05', 100, 100, 2023, 56000, 1, 23, 23),
('L24', '2024-04-06', 100, 100, 2023, 35000, 1, 24, 24),
('L25', '2024-05-01', 100, 100, 2024, 84000, 1, 25, 25),
('L26', '2024-05-02', 100, 100, 2024, 49000, 1, 26, 26),
('L27', '2024-05-03', 100, 100, 2024, 140000, 1, 27, 27),
('L28', '2024-05-04', 100, 100, 2024, 350000, 1, 28, 28),
('L29', '2024-05-05', 100, 100, 2024, 91000, 1, 29, 29),
('L30', '2024-05-06', 100, 100, 2024, 210000, 1, 30, 30);

-- 2.4 Lot Transaction (Inbound)
INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id) 
SELECT 'import', import_date, 100, 100, import_id, lot_id FROM lot;

-- =======================================================
-- 3. DỮ LIỆU BÁN HÀNG & XUẤT KHO (OUTBOUND)
-- =======================================================

-- 3.1 Sales Order
INSERT INTO sales_order (order_date, expected_date, status, customer_id, user_id) VALUES
('2024-06-01', '2024-06-03', 1, 1, 6), ('2024-06-02', '2024-06-04', 1, 2, 7), ('2024-06-03', '2024-06-05', 1, 3, 8),
('2024-06-04', '2024-06-06', 1, 4, 9), ('2024-06-05', '2024-06-07', 1, 5, 10), ('2024-06-06', '2024-06-08', 1, 6, 11),
('2024-06-07', '2024-06-09', 1, 7, 12), ('2024-06-08', '2024-06-10', 1, 8, 13), ('2024-06-09', '2024-06-11', 1, 9, 14),
('2024-06-10', '2024-06-12', 1, 10, 15), ('2024-06-11', '2024-06-13', 1, 11, 6), ('2024-06-12', '2024-06-14', 1, 12, 7),
('2024-06-13', '2024-06-15', 1, 13, 8), ('2024-06-14', '2024-06-16', 1, 14, 9), ('2024-06-15', '2024-06-17', 1, 15, 10),
('2024-06-16', '2024-06-18', 1, 16, 11), ('2024-06-17', '2024-06-19', 1, 17, 12), ('2024-06-18', '2024-06-20', 1, 18, 13),
('2024-06-19', '2024-06-21', 1, 19, 14), ('2024-06-20', '2024-06-22', 1, 20, 15), ('2024-06-21', '2024-06-23', 1, 21, 6),
('2024-06-22', '2024-06-24', 1, 22, 7), ('2024-06-23', '2024-06-25', 1, 23, 8), ('2024-06-24', '2024-06-26', 1, 24, 9),
('2024-06-25', '2024-06-27', 1, 25, 10), ('2024-06-26', '2024-06-28', 1, 26, 11), ('2024-06-27', '2024-06-29', 1, 27, 12),
('2024-06-28', '2024-06-30', 1, 28, 13), ('2024-06-29', '2024-07-01', 1, 29, 14), ('2024-06-30', '2024-07-02', 1, 30, 15);

-- 3.2 Sales Order Detail
INSERT INTO sales_order_detail (order_id, product_id, quantity) 
SELECT order_id, order_id as product_id, 2 FROM sales_order;

-- 3.3 Export Receipt
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id, order_id)
SELECT 
    DATE_ADD(order_date, INTERVAL 1 DAY),
    1, 
    0,
    3, 
    customer_id, 
    order_id 
FROM sales_order;

-- 3.4 Export Detail
INSERT INTO export_detail (export_id, product_id)
SELECT export_id, order_id as product_id FROM export_receipt;

-- 3.5 Export Lot
INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price)
SELECT 
    export_id, 
    order_id as product_id, 
    order_id as lot_id,
    2, 
    0
FROM export_receipt;

-- 3.6 Cập nhật kho
UPDATE lot SET quantity = quantity - 2;
UPDATE product SET quantity = quantity - 2;

-- 3.7 Lot Transaction (Outbound)
INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id) 
SELECT 
    'export', 
    export_date, 
    -2, 
    98,
    export_id, 
    order_id
FROM export_receipt;

-- =======================================================
-- 4. KIỂM KÊ (INVENTORY CHECK)
-- =======================================================

INSERT INTO inventory_check (check_date, status, user_id) VALUES
('2024-07-01', 1, 3), ('2024-07-02', 1, 3), ('2024-07-03', 1, 4), ('2024-07-04', 1, 4), ('2024-07-05', 1, 5);

INSERT INTO inventory_check_detail (check_id, lot_id, actual_quantity, system_quantity, difference) VALUES
(1, 1, 97, 98, -1),
(2, 2, 99, 98, 1),
(3, 3, 98, 98, 0),
(4, 4, 98, 98, 0),
(5, 5, 98, 98, 0);

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id) VALUES
('adjust', '2024-07-01', -1, 97, 1, 1),
('adjust', '2024-07-02', 1, 99, 2, 2);

UPDATE lot SET quantity = 97 WHERE lot_id = 1;
UPDATE lot SET quantity = 99 WHERE lot_id = 2;
UPDATE product SET quantity = 97 WHERE product_id = 1;
UPDATE product SET quantity = 99 WHERE product_id = 2;

-- =======================================================
-- 5. FUNCTION & PERMISSION
-- =======================================================

INSERT INTO functions (name, icon) VALUES 
('Quản lý User', 'fa-user'), ('Quản lý Role', 'fa-shield'),
('Danh mục', 'fa-list'), ('Sản phẩm', 'fa-box'),
('Nhà cung cấp', 'fa-truck'), ('Khách hàng', 'fa-users'),
('Nhập kho', 'fa-download'), ('Xuất kho', 'fa-upload'),
('Kiểm kê', 'fa-check'), ('Báo cáo tồn', 'fa-chart'),
('Báo cáo doanh thu', 'fa-dollar'), ('Cấu hình', 'fa-cog'),
('Bán hàng POS', 'fa-desktop'), ('Khuyến mãi', 'fa-gift'),
('Log hệ thống', 'fa-file-text');

INSERT INTO role_details (role_id, function_id, action) 
SELECT 1, function_id, 'FULL' FROM functions;

SET FOREIGN_KEY_CHECKS = 1;
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
    email VARCHAR(100),
    status TINYINT DEFAULT 1
);
-- 3. Bảng Customer (Khách hàng)
CREATE TABLE customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(255) NOT NULL,
    birthday DATE,
    phone VARCHAR(20),
    address VARCHAR(500),
	image VARCHAR(255) DEFAULT NULL,
	status TINYINT DEFAULT 1
);
-- 4. Bảng Roles (Phân quyền cứng)
CREATE TABLE roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1
);
-- 5. Bảng Users
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
-- 6. Bảng Product (Sách)
CREATE TABLE product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    pic varchar(255) default null,
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
-- 7. Bảng Import Receipt
CREATE TABLE import_receipt (
    import_id INT AUTO_INCREMENT PRIMARY KEY,
    import_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status TINYINT DEFAULT 1,
    total_price DECIMAL(18, 2),
    provider_id INT,
    user_id INT,
    FOREIGN KEY (provider_id) REFERENCES provider(provider_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
-- 8. Bảng Lot
CREATE TABLE lot (
    lot_id INT AUTO_INCREMENT PRIMARY KEY,
    lot_code VARCHAR(50) NOT NULL,
    import_date DATETIME,
    initial_quantity INT NOT NULL,
    quantity INT NOT NULL,
    print_year INT,
    import_price DECIMAL(18, 2),
    status enum ("Xóa", "Hết", "Lỗi", "Còn"),
    import_id INT,
    product_id INT,
    FOREIGN KEY (import_id) REFERENCES import_receipt(import_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
-- 9. Bảng Inventory Check
CREATE TABLE inventory_check (
    check_id INT AUTO_INCREMENT PRIMARY KEY,
    check_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status INT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
-- 10. Bảng Inventory Check Detail
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
-- 11. Bảng Lot Transaction
CREATE TABLE lot_transaction (
    trans_id INT AUTO_INCREMENT PRIMARY KEY,
    type enum("import", "export", "adjust"),
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    quantity_change INT,
    quantity INT,
    ref_id INT,
    lot_id INT,
    FOREIGN KEY (lot_id) REFERENCES lot(lot_id)
);
-- 12. Bảng Export Receipt (đã bỏ order_id)
CREATE TABLE export_receipt (
    export_id INT AUTO_INCREMENT PRIMARY KEY,
    export_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status INT,
    total_price DECIMAL(18, 2),
    user_id INT,
    customer_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);
-- 13. Bảng Export Detail
CREATE TABLE export_detail (
    export_id INT,
    product_id INT,
    PRIMARY KEY (export_id, product_id),
    FOREIGN KEY (export_id) REFERENCES export_receipt(export_id),
    FOREIGN KEY (product_id) REFERENCES product(product_id)
);
-- 14. Bảng Export Lot
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

-- ===================================================================
-- THÊM DỮ LIỆU MẪU CHO DATABASE KHOSACH
-- ===================================================================

USE KhoSach;

-- ===================================================================
-- 1. THÊM DỮ LIỆU CHO BẢNG CATEGORY
-- ===================================================================
INSERT INTO category (category_name, description, status) VALUES
('Văn học Việt Nam', 'Các tác phẩm văn học của tác giả Việt Nam', 1),
('Văn học nước ngoài', 'Các tác phẩm văn học dịch từ nước ngoài', 1),
('Kinh tế - Quản lý', 'Sách về kinh tế, quản trị kinh doanh', 1),
('Khoa học công nghệ', 'Sách về khoa học, công nghệ, lập trình', 1),
('Tâm lý - Kỹ năng sống', 'Sách phát triển bản thân, kỹ năng mềm', 1),
('Thiếu nhi', 'Sách dành cho trẻ em', 1),
('Giáo khoa - Tham khảo', 'Sách giáo khoa và sách tham khảo học tập', 1),
('Lịch sử - Địa lý', 'Sách về lịch sử và địa lý', 1),
('Triết học', 'Sách về triết học và tư tưởng', 1),
('Nghệ thuật - Giải trí', 'Sách về nghệ thuật, âm nhạc, điện ảnh', 1),
('Y học - Sức khỏe', 'Sách về y học và chăm sóc sức khỏe', 1),
('Nấu ăn', 'Sách hướng dẫn nấu ăn', 1),
('Du lịch', 'Sách hướng dẫn du lịch', 1),
('Truyện tranh', 'Truyện tranh và manga', 1),
('Tôn giáo - Tâm linh', 'Sách về tôn giáo và tâm linh', 1),
('Chính trị - Pháp luật', 'Sách về chính trị và pháp luật', 1),
('Ngoại ngữ', 'Sách học ngoại ngữ', 1),
('Kiến trúc - Xây dựng', 'Sách về kiến trúc và xây dựng', 1),
('Marketing - Bán hàng', 'Sách về marketing và bán hàng', 1),
('Khoa học xã hội', 'Sách về xã hội học, nhân học', 1);

-- ===================================================================
-- 2. THÊM DỮ LIỆU CHO BẢNG PROVIDER
-- ===================================================================
INSERT INTO provider (provider_name, address, phone, email) VALUES
('NXB Trẻ', '161B Lý Chính Thắng, P.Võ Thị Sáu, Q.3, TP.HCM', '0283932440', 'info@nxbtre.com.vn'),
('NXB Kim Đồng', '55 Quang Trung, Nguyễn Du, Hai Bà Trưng, Hà Nội', '0243974791', 'kimdong@nxbkimdong.com.vn'),
('NXB Văn học', '18 Nguyễn Trường Tộ, Ba Đình, Hà Nội', '0243733911', 'nxbvanhoc@hn.vnn.vn'),
('NXB Lao động', '175 Giảng Võ, Đống Đa, Hà Nội', '0438514791', 'nxblaodong@gmail.com'),
('NXB Thế Giới', '7 Nguyễn Thị Minh Khai, Q.1, TP.HCM', '0283822226', 'info@thegioipublishers.vn'),
('Công ty Alphabooks', '117-119 Lý Chính Thắng, P.7, Q.3, TP.HCM', '0283931833', 'info@alphabooks.vn'),
('NXB Đại học Quốc gia Hà Nội', '16 Hàng Chuối, Hai Bà Trưng, Hà Nội', '0243869701', 'info@vnupress.vnu.edu.vn'),
('NXB Tổng hợp TP.HCM', '62 Nguyễn Thị Minh Khai, Q.3, TP.HCM', '0283930631', 'tonghop@nxbhcm.com.vn'),
('NXB Phụ nữ', '39 Hàng Chuối, Hai Bà Trưng, Hà Nội', '0239717979', 'info@nxbphunu.com.vn'),
('NXB Hội Nhà văn', '65 Nguyễn Du, Hai Bà Trưng, Hà Nội', '0243822747', 'nxbhnv@hn.vnn.vn'),
('First News', '81 Quang Trung, P.10, Gò Vấp, TP.HCM', '0283847645', 'info@firstnews.com.vn'),
('NXB Thanh niên', '64 Bà Triệu, Hoàn Kiếm, Hà Nội', '0243942344', 'nxbthanhnien@hn.vnn.vn'),
('NXB Chính trị Quốc gia', '7 Nguyễn Thái Học, Ba Đình, Hà Nội', '0438045485', 'nxbctqg@hn.vnn.vn'),
('NXB Tri thức', '19 Nguyễn Hữu Huân, Hoàn Kiếm, Hà Nội', '0243943239', 'nxbtrithuc@hn.vnn.vn'),
('NXB Dân trí', '365 Điện Biên Phủ, Q.3, TP.HCM', '0283932216', 'contact@dantri.com.vn'),
('Công ty Thái Hà Books', '77 Thái Hà, Đống Đa, Hà Nội', '0243514234', 'info@thaihabooks.com'),
('NXB Hà Nội', '6 Tràng Thi, Hoàn Kiếm, Hà Nội', '0243825926', 'info@nxbhanoi.com.vn'),
('NXB Văn hóa Văn nghệ', '51 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội', '0243943027', 'nxbvhvn@hn.vnn.vn'),
('Công ty IPM', '55-57 Đinh Tiên Hoàng, Q.1, TP.HCM', '0283823840', 'contact@ipm.com.vn'),
('NXB Tư pháp', '58 Trần Quốc Vượng, Cầu Giấy, Hà Nội', '0243787633', 'nxbtuphap@hn.vnn.vn');

-- ===================================================================
-- 3. THÊM DỮ LIỆU CHO BẢNG CUSTOMER
-- ===================================================================
INSERT INTO customer (fullname, birthday, phone, address) VALUES
('Nguyễn Văn An', '1990-05-15', '0912345678', '123 Lê Lợi, Q.1, TP.HCM'),
('Trần Thị Bích', '1985-08-20', '0923456789', '456 Nguyễn Huệ, Q.1, TP.HCM'),
('Lê Hoàng Cường', '1995-03-10', '0934567890', '789 Hai Bà Trưng, Q.3, TP.HCM'),
('Phạm Thị Dung', '1992-11-25', '0945678901', '321 Lý Thường Kiệt, Q.10, TP.HCM'),
('Hoàng Văn Em', '1988-07-30', '0956789012', '654 Trần Hưng Đạo, Q.5, TP.HCM'),
('Vũ Thị Phượng', '1993-02-14', '0967890123', '147 Võ Văn Tần, Q.3, TP.HCM'),
('Đặng Văn Giang', '1991-09-18', '0978901234', '258 Điện Biên Phủ, Q.Bình Thạnh, TP.HCM'),
('Bùi Thị Hà', '1994-12-05', '0989012345', '369 Cách Mạng Tháng 8, Q.10, TP.HCM'),
('Ngô Văn Hùng', '1987-04-22', '0990123456', '741 Phan Xích Long, Q.Phú Nhuận, TP.HCM'),
('Dương Thị Lan', '1996-06-17', '0901234567', '852 Nguyễn Văn Cừ, Q.5, TP.HCM'),
('Trịnh Văn Minh', '1989-01-08', '0911223344', '963 Lạc Long Quân, Q.11, TP.HCM'),
('Lý Thị Nga', '1997-10-28', '0922334455', '159 Hoàng Văn Thụ, Q.Tân Bình, TP.HCM'),
('Phan Văn Phú', '1986-03-31', '0933445566', '357 Sư Vạn Hạnh, Q.10, TP.HCM'),
('Mai Thị Quỳnh', '1998-08-12', '0944556677', '486 Ba Tháng Hai, Q.10, TP.HCM'),
('Võ Văn Sang', '1984-05-19', '0955667788', '597 Phan Đăng Lưu, Q.Phú Nhuận, TP.HCM'),
('Đỗ Thị Thảo', '1999-11-03', '0966778899', '168 Tô Hiến Thành, Q.10, TP.HCM'),
('Cao Văn Tùng', '1983-07-26', '0977889900', '279 Trường Chinh, Q.Tân Bình, TP.HCM'),
('Hồ Thị Uyên', '2000-02-09', '0988990011', '381 Xô Viết Nghệ Tĩnh, Q.Bình Thạnh, TP.HCM'),
('Tô Văn Việt', '1982-09-15', '0999001122', '492 Cộng Hòa, Q.Tân Bình, TP.HCM'),
('Lưu Thị Xuân', '2001-04-21', '0900112233', '513 Phan Văn Trị, Q.Gò Vấp, TP.HCM');

-- ===================================================================
-- 4. THÊM DỮ LIỆU CHO BẢNG ROLES (Phân quyền cứng)
-- ===================================================================
INSERT INTO roles (name, status) VALUES
('Admin', 1),
('Quản lý kho', 1),
('Nhân viên nhập hàng', 1),
('Nhân viên bán hàng', 1),
('Kế toán', 1),
('Thủ kho', 1),
('Giám đốc', 1),
('Nhân viên kiểm kê', 1),
('Trưởng phòng kinh doanh', 1),
('Nhân viên CSKH', 1),
('Trưởng phòng kho', 1),
('Nhân viên marketing', 1),
('Thủ quỹ', 1),
('Trưởng phòng kế toán', 1),
('Nhân viên IT', 1),
('Bảo vệ', 1),
('Lái xe', 1),
('Tạp vụ', 1),
('Thực tập sinh', 1),
('Cộng tác viên', 1);

-- ===================================================================
-- 5. THÊM DỮ LIỆU CHO BẢNG USERS
-- ===================================================================
INSERT INTO users (role_id, username, password, avatar, fullname, phone, email, address, status) VALUES
(1, 'admin', '123456', 'avatar1.jpg', 'Nguyễn Văn Admin', '0901111111', 'admin@khosach.vn', '123 Admin Street, Q.1, TP.HCM', 1),
(2, 'qlkho01', '123456', 'avatar2.jpg', 'Trần Thị Kho', '0902222222', 'qlkho01@khosach.vn', '456 Warehouse Rd, Q.3, TP.HCM', 1),
(3, 'nvnhap01', '123456', 'avatar3.jpg', 'Lê Văn Nhập', '0903333333', 'nvnhap01@khosach.vn', '789 Import St, Q.5, TP.HCM', 1),
(4, 'nvban01', '123456', 'avatar4.jpg', 'Phạm Thị Bán', '0904444444', 'nvban01@khosach.vn', '321 Sales Ave, Q.10, TP.HCM', 1),
(5, 'ketoan01', '123456', 'avatar5.jpg', 'Hoàng Văn Toán', '0905555555', 'ketoan01@khosach.vn', '654 Account Blvd, Q.Tân Bình, TP.HCM', 1),
(6, 'thukho01', '123456', 'avatar6.jpg', 'Vũ Thị Thủ', '0906666666', 'thukho01@khosach.vn', '147 Stock Rd, Q.Bình Thạnh, TP.HCM', 1),
(7, 'giamdoc01', '123456', 'avatar7.jpg', 'Đặng Văn Giám', '0907777777', 'giamdoc@khosach.vn', '258 Director St, Q.1, TP.HCM', 1),
(8, 'nvkiemke01', '123456', 'avatar8.jpg', 'Bùi Thị Kiểm', '0908888888', 'nvkiemke01@khosach.vn', '369 Audit Lane, Q.3, TP.HCM', 1),
(9, 'tpkinhdoanh', '123456', 'avatar9.jpg', 'Ngô Văn Kinh', '0909999999', 'tpkd@khosach.vn', '741 Business Rd, Q.5, TP.HCM', 1),
(10, 'nvcskh01', '123456', 'avatar10.jpg', 'Dương Thị Chăm', '0910101010', 'cskh01@khosach.vn', '852 Service St, Q.10, TP.HCM', 1),
(11, 'tpkho01', '123456', 'avatar11.jpg', 'Trịnh Văn Trưởng', '0911111111', 'tpkho01@khosach.vn', '963 Warehouse Mgr Rd, Q.Gò Vấp, TP.HCM', 1),
(12, 'nvmarketing', '123456', 'avatar12.jpg', 'Lý Thị Marketing', '0912121212', 'marketing01@khosach.vn', '159 Marketing Ave, Q.Phú Nhuận, TP.HCM', 1),
(13, 'thuquy01', '123456', 'avatar13.jpg', 'Phan Văn Quỹ', '0913131313', 'thuquy01@khosach.vn', '357 Cashier St, Q.1, TP.HCM', 1),
(14, 'tpketoan', '123456', 'avatar14.jpg', 'Mai Thị Kế', '0914141414', 'tpketoan@khosach.vn', '486 Accounting Rd, Q.3, TP.HCM', 1),
(15, 'nvit01', '123456', 'avatar15.jpg', 'Võ Văn IT', '0915151515', 'nvit01@khosach.vn', '597 Tech Lane, Q.Tân Bình, TP.HCM', 1),
(3, 'nvnhap02', '123456', 'avatar16.jpg', 'Đỗ Thị Nhập 2', '0916161616', 'nvnhap02@khosach.vn', '168 Import St 2, Q.5, TP.HCM', 1),
(4, 'nvban02', '123456', 'avatar17.jpg', 'Cao Văn Bán 2', '0917171717', 'nvban02@khosach.vn', '279 Sales Ave 2, Q.10, TP.HCM', 1),
(4, 'nvban03', '123456', 'avatar18.jpg', 'Hồ Thị Bán 3', '0918181818', 'nvban03@khosach.vn', '381 Sales Ave 3, Q.10, TP.HCM', 1),
(6, 'thukho02', '123456', 'avatar19.jpg', 'Tô Văn Thủ 2', '0919191919', 'thukho02@khosach.vn', '492 Stock Rd 2, Q.Bình Thạnh, TP.HCM', 1),
(10, 'nvcskh02', '123456', 'avatar20.jpg', 'Lưu Thị Chăm 2', '0920202020', 'cskh02@khosach.vn', '513 Service St 2, Q.10, TP.HCM', 1);

-- ===================================================================
-- 6. THÊM DỮ LIỆU CHO BẢNG PRODUCT
-- ===================================================================
INSERT INTO product (product_name, pic, publisher, publish_year, author, language, price, quantity, status, category_id) VALUES
('Đắc Nhân Tâm', 'dacnhantam.jpg', 'NXB Tổng hợp TP.HCM', 2020, 'Dale Carnegie', 'Tiếng Việt', 86000, 0, 1, 5),
('Nhà Giả Kim', 'nhagiakim.jpg', 'NXB Hội Nhà văn', 2019, 'Paulo Coelho', 'Tiếng Việt', 79000, 0, 1, 2),
('Sapiens: Lược Sử Loài Người', 'sapiens.jpg', 'NXB Trẻ', 2018, 'Yuval Noah Harari', 'Tiếng Việt', 189000, 0, 1, 8),
('Tuổi Trẻ Đáng Giá Bao Nhiêu', 'tuoitredanggiabaonhieu.jpg', 'NXB Hội Nhà văn', 2021, 'Rosie Nguyễn', 'Tiếng Việt', 80000, 0, 1, 5),
('Atomic Habits', 'atomichabits.jpg', 'NXB Thế Giới', 2020, 'James Clear', 'Tiếng Việt', 179000, 0, 1, 5),
('Tư Duy Nhanh Và Chậm', 'tuduynh anhvacham.jpg', 'NXB Thế Giới', 2019, 'Daniel Kahneman', 'Tiếng Việt', 239000, 0, 1, 5),
('Rich Dad Poor Dad', 'richdadpoordad.jpg', 'NXB Lao động', 2018, 'Robert Kiyosaki', 'Tiếng Việt', 149000, 0, 1, 3),
('Zero To One', 'zerotoone.jpg', 'NXB Trẻ', 2020, 'Peter Thiel', 'Tiếng Việt', 169000, 0, 1, 3),
('Harry Potter và Hòn Đá Phù Thủy', 'harrypotter1.jpg', 'NXB Trẻ', 2017, 'J.K. Rowling', 'Tiếng Việt', 189000, 0, 1, 2),
('Bố Già', 'bogia.jpg', 'NXB Văn học', 2018, 'Mario Puzo', 'Tiếng Việt', 139000, 0, 1, 2),
('Lập Trình Python Cơ Bản', 'python.jpg', 'NXB Đại học Quốc gia HN', 2021, 'Nguyễn Văn A', 'Tiếng Việt', 99000, 0, 1, 4),
('Truyện Kiều', 'truyenkieu.jpg', 'NXB Văn học', 2019, 'Nguyễn Du', 'Tiếng Việt', 65000, 0, 1, 1),
('Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'hoavang coxanh.jpg', 'NXB Trẻ', 2018, 'Nguyễn Nhật Ánh', 'Tiếng Việt', 95000, 0, 1, 1),
('Cho Tôi Xin Một Vé Đi Tuổi Thơ', 'vetuoitho.jpg', 'NXB Trẻ', 2017, 'Nguyễn Nhật Ánh', 'Tiếng Việt', 85000, 0, 1, 1),
('Marketing 4.0', 'marketing40.jpg', 'NXB Trẻ', 2020, 'Philip Kotler', 'Tiếng Việt', 179000, 0, 1, 19),
('Quản Trị Học', 'quantrihoc.jpg', 'NXB Thống kê', 2019, 'GS. TS. Phạm Văn B', 'Tiếng Việt', 125000, 0, 1, 3),
('Doraemon Tập 1', 'doraemon1.jpg', 'NXB Kim Đồng', 2020, 'Fujiko F. Fujio', 'Tiếng Việt', 18000, 0, 1, 14),
('Conan Tập 100', 'conan100.jpg', 'NXB Kim Đồng', 2021, 'Aoyama Gosho', 'Tiếng Việt', 22000, 0, 1, 14),
('Luyện Thi TOEIC 900+', 'toeic900.jpg', 'NXB Tổng hợp TP.HCM', 2020, 'Đội ngũ biên soạn', 'Tiếng Việt', 199000, 0, 1, 17),
('English Grammar In Use', 'grammar.jpg', 'Cambridge', 2019, 'Raymond Murphy', 'Tiếng Anh', 289000, 0, 1, 17);

-- ===================================================================
-- 7. THÊM DỮ LIỆU CHO PHIẾU NHẬP VÀ LÔ HÀNG (VỚI CẬP NHẬT SỐ LƯỢNG)
-- ===================================================================

-- Phiếu nhập 1
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-01-15 08:30:00', 1, 45000000, 1, 3);
SET @import_id_1 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024011501', '2024-01-15 08:30:00', 100, 100, 2023, 70000, 'Còn', @import_id_1, 1),
('LOT2024011502', '2024-01-15 08:30:00', 120, 120, 2023, 65000, 'Còn', @import_id_1, 2),
('LOT2024011503', '2024-01-15 08:30:00', 80, 80, 2023, 150000, 'Còn', @import_id_1, 3);

UPDATE product SET quantity = quantity + 100 WHERE product_id = 1;
UPDATE product SET quantity = quantity + 120 WHERE product_id = 2;
UPDATE product SET quantity = quantity + 80 WHERE product_id = 3;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-01-15 08:30:00', initial_quantity, quantity, @import_id_1, lot_id FROM lot WHERE import_id = @import_id_1;

-- Phiếu nhập 2
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-01-20 09:00:00', 1, 38000000, 2, 3);
SET @import_id_2 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024012001', '2024-01-20 09:00:00', 150, 150, 2023, 65000, 'Còn', @import_id_2, 4),
('LOT2024012002', '2024-01-20 09:00:00', 100, 100, 2023, 140000, 'Còn', @import_id_2, 5),
('LOT2024012003', '2024-01-20 09:00:00', 60, 60, 2023, 200000, 'Còn', @import_id_2, 6);

UPDATE product SET quantity = quantity + 150 WHERE product_id = 4;
UPDATE product SET quantity = quantity + 100 WHERE product_id = 5;
UPDATE product SET quantity = quantity + 60 WHERE product_id = 6;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-01-20 09:00:00', initial_quantity, quantity, @import_id_2, lot_id FROM lot WHERE import_id = @import_id_2;

-- Phiếu nhập 3
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-01-25 10:15:00', 1, 42000000, 3, 16);
SET @import_id_3 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024012501', '2024-01-25 10:15:00', 90, 90, 2023, 120000, 'Còn', @import_id_3, 7),
('LOT2024012502', '2024-01-25 10:15:00', 75, 75, 2023, 135000, 'Còn', @import_id_3, 8),
('LOT2024012503', '2024-01-25 10:15:00', 110, 110, 2023, 150000, 'Còn', @import_id_3, 9);

UPDATE product SET quantity = quantity + 90 WHERE product_id = 7;
UPDATE product SET quantity = quantity + 75 WHERE product_id = 8;
UPDATE product SET quantity = quantity + 110 WHERE product_id = 9;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-01-25 10:15:00', initial_quantity, quantity, @import_id_3, lot_id FROM lot WHERE import_id = @import_id_3;

-- Phiếu nhập 4
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-02-01 08:45:00', 1, 35000000, 4, 3);
SET @import_id_4 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024020101', '2024-02-01 08:45:00', 85, 85, 2023, 115000, 'Còn', @import_id_4, 10),
('LOT2024020102', '2024-02-01 08:45:00', 130, 130, 2023, 80000, 'Còn', @import_id_4, 11),
('LOT2024020103', '2024-02-01 08:45:00', 95, 95, 2023, 55000, 'Còn', @import_id_4, 12);

UPDATE product SET quantity = quantity + 85 WHERE product_id = 10;
UPDATE product SET quantity = quantity + 130 WHERE product_id = 11;
UPDATE product SET quantity = quantity + 95 WHERE product_id = 12;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-02-01 08:45:00', initial_quantity, quantity, @import_id_4, lot_id FROM lot WHERE import_id = @import_id_4;

-- Phiếu nhập 5
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-02-05 09:30:00', 1, 48000000, 1, 16);
SET @import_id_5 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024020501', '2024-02-05 09:30:00', 125, 125, 2024, 75000, 'Còn', @import_id_5, 13),
('LOT2024020502', '2024-02-05 09:30:00', 105, 105, 2024, 70000, 'Còn', @import_id_5, 14),
('LOT2024020503', '2024-02-05 09:30:00', 140, 140, 2024, 145000, 'Còn', @import_id_5, 15);

UPDATE product SET quantity = quantity + 125 WHERE product_id = 13;
UPDATE product SET quantity = quantity + 105 WHERE product_id = 14;
UPDATE product SET quantity = quantity + 140 WHERE product_id = 15;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-02-05 09:30:00', initial_quantity, quantity, @import_id_5, lot_id FROM lot WHERE import_id = @import_id_5;

-- Phiếu nhập 6
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-02-10 11:00:00', 1, 25000000, 5, 3);
SET @import_id_6 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024021001', '2024-02-10 11:00:00', 115, 115, 2024, 105000, 'Còn', @import_id_6, 16),
('LOT2024021002', '2024-02-10 11:00:00', 200, 200, 2024, 14000, 'Còn', @import_id_6, 17),
('LOT2024021003', '2024-02-10 11:00:00', 180, 180, 2024, 18000, 'Còn', @import_id_6, 18);

UPDATE product SET quantity = quantity + 115 WHERE product_id = 16;
UPDATE product SET quantity = quantity + 200 WHERE product_id = 17;
UPDATE product SET quantity = quantity + 180 WHERE product_id = 18;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-02-10 11:00:00', initial_quantity, quantity, @import_id_6, lot_id FROM lot WHERE import_id = @import_id_6;

-- Phiếu nhập 7
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-02-15 08:20:00', 1, 52000000, 6, 16);
SET @import_id_7 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024021501', '2024-02-15 08:20:00', 95, 95, 2024, 165000, 'Còn', @import_id_7, 19),
('LOT2024021502', '2024-02-15 08:20:00', 75, 75, 2024, 240000, 'Còn', @import_id_7, 20),
('LOT2024021503', '2024-02-15 08:20:00', 100, 100, 2024, 70000, 'Còn', @import_id_7, 1);

UPDATE product SET quantity = quantity + 95 WHERE product_id = 19;
UPDATE product SET quantity = quantity + 75 WHERE product_id = 20;
UPDATE product SET quantity = quantity + 100 WHERE product_id = 1;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-02-15 08:20:00', initial_quantity, quantity, @import_id_7, lot_id FROM lot WHERE import_id = @import_id_7;

-- Phiếu nhập 8
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-02-20 10:00:00', 1, 39000000, 7, 3);
SET @import_id_8 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024022001', '2024-02-20 10:00:00', 110, 110, 2024, 65000, 'Còn', @import_id_8, 2),
('LOT2024022002', '2024-02-20 10:00:00', 88, 88, 2024, 150000, 'Còn', @import_id_8, 3),
('LOT2024022003', '2024-02-20 10:00:00', 125, 125, 2024, 65000, 'Còn', @import_id_8, 4);

UPDATE product SET quantity = quantity + 110 WHERE product_id = 2;
UPDATE product SET quantity = quantity + 88 WHERE product_id = 3;
UPDATE product SET quantity = quantity + 125 WHERE product_id = 4;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-02-20 10:00:00', initial_quantity, quantity, @import_id_8, lot_id FROM lot WHERE import_id = @import_id_8;

-- Phiếu nhập 9
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-02-25 09:15:00', 1, 44000000, 8, 16);
SET @import_id_9 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024022501', '2024-02-25 09:15:00', 92, 92, 2024, 140000, 'Còn', @import_id_9, 5),
('LOT2024022502', '2024-02-25 09:15:00', 78, 78, 2024, 200000, 'Còn', @import_id_9, 6),
('LOT2024022503', '2024-02-25 09:15:00', 105, 105, 2024, 120000, 'Còn', @import_id_9, 7);

UPDATE product SET quantity = quantity + 92 WHERE product_id = 5;
UPDATE product SET quantity = quantity + 78 WHERE product_id = 6;
UPDATE product SET quantity = quantity + 105 WHERE product_id = 7;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-02-25 09:15:00', initial_quantity, quantity, @import_id_9, lot_id FROM lot WHERE import_id = @import_id_9;

-- Phiếu nhập 10
INSERT INTO import_receipt (import_date, status, total_price, provider_id, user_id) 
VALUES ('2024-03-01 08:00:00', 1, 36000000, 9, 3);
SET @import_id_10 = LAST_INSERT_ID();

INSERT INTO lot (lot_code, import_date, initial_quantity, quantity, print_year, import_price, status, import_id, product_id)
VALUES 
('LOT2024030101', '2024-03-01 08:00:00', 82, 82, 2024, 135000, 'Còn', @import_id_10, 8),
('LOT2024030102', '2024-03-01 08:00:00', 98, 98, 2024, 150000, 'Còn', @import_id_10, 9),
('LOT2024030103', '2024-03-01 08:00:00', 70, 70, 2024, 115000, 'Còn', @import_id_10, 10);

UPDATE product SET quantity = quantity + 82 WHERE product_id = 8;
UPDATE product SET quantity = quantity + 98 WHERE product_id = 9;
UPDATE product SET quantity = quantity + 70 WHERE product_id = 10;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
SELECT 'import', '2024-03-01 08:00:00', initial_quantity, quantity, @import_id_10, lot_id FROM lot WHERE import_id = @import_id_10;

-- ===================================================================
-- 8. THÊM DỮ LIỆU CHO PHIẾU XUẤT (đã bỏ order_id)
-- ===================================================================

-- Phiếu xuất 1
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-01-17 10:30:00', 1, 667000, 4, 1);
SET @export_id_1 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_1, 1), (@export_id_1, 2);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_1, 1, 1, 5, 86000),
(@export_id_1, 2, 2, 3, 79000);

UPDATE lot SET quantity = quantity - 5 WHERE lot_id = 1;
UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 2;
UPDATE product SET quantity = quantity - 5 WHERE product_id = 1;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 2;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-01-17 10:30:00', -5, (SELECT quantity FROM lot WHERE lot_id = 1), @export_id_1, 1),
('export', '2024-01-17 10:30:00', -3, (SELECT quantity FROM lot WHERE lot_id = 2), @export_id_1, 2);

-- Phiếu xuất 2
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-01-23 11:00:00', 1, 698000, 4, 2);
SET @export_id_2 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_2, 3), (@export_id_2, 4);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_2, 3, 3, 2, 189000),
(@export_id_2, 4, 4, 4, 80000);

UPDATE lot SET quantity = quantity - 2 WHERE lot_id = 3;
UPDATE lot SET quantity = quantity - 4 WHERE lot_id = 4;
UPDATE product SET quantity = quantity - 2 WHERE product_id = 3;
UPDATE product SET quantity = quantity - 4 WHERE product_id = 4;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-01-23 11:00:00', -2, (SELECT quantity FROM lot WHERE lot_id = 3), @export_id_2, 3),
('export', '2024-01-23 11:00:00', -4, (SELECT quantity FROM lot WHERE lot_id = 4), @export_id_2, 4);

-- Phiếu xuất 3
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-01-28 14:15:00', 1, 1015000, 17, 3);
SET @export_id_3 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_3, 5), (@export_id_3, 6);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_3, 5, 5, 3, 179000),
(@export_id_3, 6, 6, 2, 239000);

UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 5;
UPDATE lot SET quantity = quantity - 2 WHERE lot_id = 6;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 5;
UPDATE product SET quantity = quantity - 2 WHERE product_id = 6;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-01-28 14:15:00', -3, (SELECT quantity FROM lot WHERE lot_id = 5), @export_id_3, 5),
('export', '2024-01-28 14:15:00', -2, (SELECT quantity FROM lot WHERE lot_id = 6), @export_id_3, 6);

-- Phiếu xuất 4
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-02-03 09:30:00', 1, 934000, 4, 4);
SET @export_id_4 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_4, 7), (@export_id_4, 8);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_4, 7, 7, 4, 149000),
(@export_id_4, 8, 8, 2, 169000);

UPDATE lot SET quantity = quantity - 4 WHERE lot_id = 7;
UPDATE lot SET quantity = quantity - 2 WHERE lot_id = 8;
UPDATE product SET quantity = quantity - 4 WHERE product_id = 7;
UPDATE product SET quantity = quantity - 2 WHERE product_id = 8;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-02-03 09:30:00', -4, (SELECT quantity FROM lot WHERE lot_id = 7), @export_id_4, 7),
('export', '2024-02-03 09:30:00', -2, (SELECT quantity FROM lot WHERE lot_id = 8), @export_id_4, 8);

-- Phiếu xuất 5
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-02-08 15:45:00', 1, 845000, 17, 5);
SET @export_id_5 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_5, 9), (@export_id_5, 10);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_5, 9, 9, 3, 189000),
(@export_id_5, 10, 10, 2, 139000);

UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 9;
UPDATE lot SET quantity = quantity - 2 WHERE lot_id = 10;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 9;
UPDATE product SET quantity = quantity - 2 WHERE product_id = 10;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-02-08 15:45:00', -3, (SELECT quantity FROM lot WHERE lot_id = 9), @export_id_5, 9),
('export', '2024-02-08 15:45:00', -2, (SELECT quantity FROM lot WHERE lot_id = 10), @export_id_5, 10);

-- Phiếu xuất 6
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-02-13 10:15:00', 1, 690000, 18, 6);
SET @export_id_6 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_6, 11), (@export_id_6, 12);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_6, 11, 11, 5, 99000),
(@export_id_6, 12, 12, 3, 65000);

UPDATE lot SET quantity = quantity - 5 WHERE lot_id = 11;
UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 12;
UPDATE product SET quantity = quantity - 5 WHERE product_id = 11;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 12;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-02-13 10:15:00', -5, (SELECT quantity FROM lot WHERE lot_id = 11), @export_id_6, 11),
('export', '2024-02-13 10:15:00', -3, (SELECT quantity FROM lot WHERE lot_id = 12), @export_id_6, 12);

-- Phiếu xuất 7
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-02-18 13:30:00', 1, 635000, 4, 7);
SET @export_id_7 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_7, 13), (@export_id_7, 14);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_7, 13, 13, 4, 95000),
(@export_id_7, 14, 14, 3, 85000);

UPDATE lot SET quantity = quantity - 4 WHERE lot_id = 13;
UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 14;
UPDATE product SET quantity = quantity - 4 WHERE product_id = 13;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 14;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-02-18 13:30:00', -4, (SELECT quantity FROM lot WHERE lot_id = 13), @export_id_7, 13),
('export', '2024-02-18 13:30:00', -3, (SELECT quantity FROM lot WHERE lot_id = 14), @export_id_7, 14);

-- Phiếu xuất 8
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-02-23 08:50:00', 1, 733000, 17, 8);
SET @export_id_8 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_8, 15), (@export_id_8, 16);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_8, 15, 15, 2, 179000),
(@export_id_8, 16, 16, 3, 125000);

UPDATE lot SET quantity = quantity - 2 WHERE lot_id = 15;
UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 16;
UPDATE product SET quantity = quantity - 2 WHERE product_id = 15;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 16;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-02-23 08:50:00', -2, (SELECT quantity FROM lot WHERE lot_id = 15), @export_id_8, 15),
('export', '2024-02-23 08:50:00', -3, (SELECT quantity FROM lot WHERE lot_id = 16), @export_id_8, 16);

-- Phiếu xuất 9
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-02-28 16:20:00', 1, 356000, 18, 9);
SET @export_id_9 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_9, 17), (@export_id_9, 18);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_9, 17, 17, 10, 18000),
(@export_id_9, 18, 18, 8, 22000);

UPDATE lot SET quantity = quantity - 10 WHERE lot_id = 17;
UPDATE lot SET quantity = quantity - 8 WHERE lot_id = 18;
UPDATE product SET quantity = quantity - 10 WHERE product_id = 17;
UPDATE product SET quantity = quantity - 8 WHERE product_id = 18;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-02-28 16:20:00', -10, (SELECT quantity FROM lot WHERE lot_id = 17), @export_id_9, 17),
('export', '2024-02-28 16:20:00', -8, (SELECT quantity FROM lot WHERE lot_id = 18), @export_id_9, 18);

-- Phiếu xuất 10
INSERT INTO export_receipt (export_date, status, total_price, user_id, customer_id)
VALUES ('2024-03-04 11:40:00', 1, 1175000, 4, 10);
SET @export_id_10 = LAST_INSERT_ID();

INSERT INTO export_detail (export_id, product_id) VALUES (@export_id_10, 19), (@export_id_10, 20);

INSERT INTO export_lot (export_id, product_id, lot_id, quantity, export_price) VALUES
(@export_id_10, 19, 19, 3, 199000),
(@export_id_10, 20, 20, 2, 289000);

UPDATE lot SET quantity = quantity - 3 WHERE lot_id = 19;
UPDATE lot SET quantity = quantity - 2 WHERE lot_id = 20;
UPDATE product SET quantity = quantity - 3 WHERE product_id = 19;
UPDATE product SET quantity = quantity - 2 WHERE product_id = 20;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES 
('export', '2024-03-04 11:40:00', -3, (SELECT quantity FROM lot WHERE lot_id = 19), @export_id_10, 19),
('export', '2024-03-04 11:40:00', -2, (SELECT quantity FROM lot WHERE lot_id = 20), @export_id_10, 20);

-- ===================================================================
-- 9. THÊM DỮ LIỆU CHO KIỂM KÊ (VỚI CẬP NHẬT SỐ LƯỢNG)
-- ===================================================================

-- Kiểm kê lần 1
INSERT INTO inventory_check (check_date, status, user_id)
VALUES ('2024-02-29 17:00:00', 1, 8);
SET @check_id_1 = LAST_INSERT_ID();

INSERT INTO inventory_check_detail (check_id, lot_id, actual_quantity, system_quantity, difference) VALUES
(@check_id_1, 1, 94, 95, -1),
(@check_id_1, 2, 117, 117, 0),
(@check_id_1, 3, 78, 78, 0),
(@check_id_1, 4, 145, 146, -1),
(@check_id_1, 5, 97, 97, 0);

UPDATE lot SET quantity = 94 WHERE lot_id = 1;
UPDATE lot SET quantity = 145 WHERE lot_id = 4;
UPDATE product SET quantity = quantity - 1 WHERE product_id = 1;
UPDATE product SET quantity = quantity - 1 WHERE product_id = 4;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id) VALUES
('adjust', '2024-02-29 17:00:00', -1, 94, @check_id_1, 1),
('adjust', '2024-02-29 17:00:00', -1, 145, @check_id_1, 4);

-- Kiểm kê lần 2
INSERT INTO inventory_check (check_date, status, user_id)
VALUES ('2024-03-15 16:30:00', 1, 8);
SET @check_id_2 = LAST_INSERT_ID();

INSERT INTO inventory_check_detail (check_id, lot_id, actual_quantity, system_quantity, difference) VALUES
(@check_id_2, 6, 58, 58, 0),
(@check_id_2, 7, 85, 86, -1),
(@check_id_2, 8, 73, 73, 0),
(@check_id_2, 9, 108, 107, 1),
(@check_id_2, 10, 83, 83, 0);

UPDATE lot SET quantity = 85 WHERE lot_id = 7;
UPDATE lot SET quantity = 108 WHERE lot_id = 9;
UPDATE product SET quantity = quantity - 1 WHERE product_id = 7;
UPDATE product SET quantity = quantity + 1 WHERE product_id = 9;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id) VALUES
('adjust', '2024-03-15 16:30:00', -1, 85, @check_id_2, 7),
('adjust', '2024-03-15 16:30:00', 1, 108, @check_id_2, 9);

-- Kiểm kê lần 3
INSERT INTO inventory_check (check_date, status, user_id)
VALUES ('2024-04-10 15:45:00', 1, 8);
SET @check_id_3 = LAST_INSERT_ID();

INSERT INTO inventory_check_detail (check_id, lot_id, actual_quantity, system_quantity, difference) VALUES
(@check_id_3, 11, 125, 125, 0),
(@check_id_3, 12, 91, 92, -1),
(@check_id_3, 13, 121, 121, 0),
(@check_id_3, 14, 102, 102, 0),
(@check_id_3, 15, 138, 138, 0);

UPDATE lot SET quantity = 91 WHERE lot_id = 12;
UPDATE product SET quantity = quantity - 1 WHERE product_id = 12;

INSERT INTO lot_transaction (type, date, quantity_change, quantity, ref_id, lot_id)
VALUES ('adjust', '2024-04-10 15:45:00', -1, 91, @check_id_3, 12);

-- ===================================================================
-- KẾT THÚC THÊM DỮ LIỆU
-- ===================================================================

-- Kiểm tra số lượng cuối cùng
SELECT 'Tổng kết số lượng sản phẩm' AS Title;
SELECT product_id, product_name, quantity FROM product ORDER BY product_id;

SELECT 'Tổng kết số lượng lô hàng' AS Title;
SELECT lot_id, lot_code, product_id, quantity, status FROM lot ORDER BY lot_id;

SELECT 'Tổng số giao dịch lô' AS Title;
SELECT type, COUNT(*) as total FROM lot_transaction GROUP BY type;

COMMIT;
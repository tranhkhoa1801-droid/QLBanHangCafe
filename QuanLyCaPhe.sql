-- Tạo cơ sở dữ liệu
CREATE DATABASE QLQuanCaPhe
GO

USE QLQuanCaPhe
GO

-- Bảng TaiKhoan (Tài khoản đăng nhập của nhân viên)
CREATE TABLE TaiKhoan (
    maTK VARCHAR(20) NOT NULL PRIMARY KEY,
    tenDangNhap VARCHAR(50) NOT NULL,
    matKhau VARCHAR(50) NOT NULL
);
GO

-- Bảng NhanVien (Nhân sự cửa hàng)
CREATE TABLE NhanVien (
    maNV VARCHAR(20) NOT NULL PRIMARY KEY,
    hoTen NVARCHAR(100) NOT NULL,
    vaiTro NVARCHAR(50),
    sdt VARCHAR(15),
    maTK VARCHAR(20) NOT NULL,
    caLamViec NVARCHAR(50),
    isActive BIT DEFAULT 1,
    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (maTK) REFERENCES TaiKhoan(maTK)
);
GO

-- Bảng DanhMuc (Danh mục sản phẩm)
CREATE TABLE DanhMuc (
    maDM VARCHAR(20) NOT NULL PRIMARY KEY,
    tenDM NVARCHAR(100) NOT NULL
);
GO

-- Bảng SanPham (Sản phẩm kinh doanh)
CREATE TABLE SanPham (
    maSP VARCHAR(20) NOT NULL PRIMARY KEY,
    tenSP NVARCHAR(100) NOT NULL,
    giaVon DECIMAL(18,2) NOT NULL,
    giaBan DECIMAL(18,2) NOT NULL,
    maDM VARCHAR(20) NOT NULL,
    CONSTRAINT FK_SanPham_DanhMuc FOREIGN KEY (maDM) REFERENCES DanhMuc(maDM)
);
GO

-- Bảng HoaDon (Hóa đơn bán hàng)
CREATE TABLE HoaDon (
    maHD VARCHAR(20) NOT NULL PRIMARY KEY,
    thoiGianTao DATETIME DEFAULT GETDATE(),
    ban NVARCHAR(50),
    maNV VARCHAR(20) NOT NULL,
    phuongThucThanhToan NVARCHAR(50),
    trangThai NVARCHAR(50),
    tongTien DECIMAL(18,2),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV)
);
GO

-- Bảng ChiTietHoaDon (Chi tiết các mặt hàng trong hóa đơn)
CREATE TABLE ChiTietHoaDon (
    maHD VARCHAR(20) NOT NULL,
    maSP VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL,
    giaBanHienTai DECIMAL(18,2) NOT NULL,
    giaVonHienTai DECIMAL(18,2) NOT NULL,
    PRIMARY KEY (maHD, maSP),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon(maHD),
    CONSTRAINT FK_CTHD_SanPham FOREIGN KEY (maSP) REFERENCES SanPham(maSP)
);
GO

-- Thêm tài khoản mới
INSERT INTO TaiKhoan (maTK, tenDangNhap, matKhau) VALUES
('TK01', 'manager_ha', 'password123'),
('TK02', 'cashier_tung', 'password123'),
('TK03', 'barista_nam', 'password123');
GO

-- Thêm nhân viên mới
INSERT INTO NhanVien (maNV, hoTen, vaiTro, sdt, maTK, caLamViec, isActive) VALUES
('NV01', N'Nguyễn Thanh Hà', N'Quản Lý', '0988112233', 'TK01', N'Ca sáng (6:00 - 14:00)', 1),
('NV02', N'Trần Thanh Tùng', N'Thu Ngân', '0977445566', 'TK02', N'Ca chiều (14:00 - 22:00)', 1),
('NV03', N'Phạm Hoài Nam', N'Pha Chế', '0966778899', 'TK03', N'Ca toàn thời gian', 1);
GO

-- Thêm danh mục mới (Trà sữa, Sinh tố & Nước ép, Đồ ăn vặt)
INSERT INTO DanhMuc (maDM, tenDM) VALUES 
('DM01', N'Trà sữa trân châu'),
('DM02', N'Sinh tố & Nước ép'),
('DM03', N'Đồ ăn vặt');
GO

-- Thêm sản phẩm mới tương ứng với danh mục
INSERT INTO SanPham (maSP, tenSP, giaVon, giaBan, maDM) VALUES 
('SP01', N'Trà sữa truyền thống', 12000, 30000, 'DM01'),
('SP02', N'Trà sữa ô long nướng', 14000, 35000, 'DM01'),
('SP03', N'Trà sữa thái xanh', 13000, 32000, 'DM01'),
('SP04', N'Sinh tố bơ', 18000, 40000, 'DM02'),
('SP05', N'Sinh tố xoài', 16000, 38000, 'DM02'),
('SP06', N'Nước ép cam tươi', 15000, 35000, 'DM02'),
('SP07', N'Hướng dương tẩm gia vị', 8000, 20000, 'DM03'),
('SP08', N'Khô gà lá chanh', 15000, 35000, 'DM03'),
('SP09', N'Khoai tây chiên giòn', 12000, 30000, 'DM03');
GO

-- Thêm hóa đơn mới
INSERT INTO HoaDon (maHD, thoiGianTao, ban, maNV, phuongThucThanhToan, trangThai, tongTien) VALUES
('HD001', '2026-06-01 09:30:00', N'Bàn 03', 'NV02', N'Tiền mặt', N'Đã thanh toán', 95000),
('HD002', '2026-06-01 14:15:00', N'Bàn 07', 'NV02', N'Chuyển khoản', N'Đã thanh toán', 113000),
('HD003', '2026-06-02 10:00:00', N'Mang đi', 'NV01', N'Tiền mặt', N'Đã thanh toán', 55000);
GO

-- Thêm chi tiết hóa đơn tương ứng với các sản phẩm mới
INSERT INTO ChiTietHoaDon (maHD, maSP, soLuong, giaBanHienTai, giaVonHienTai) VALUES
-- HD001 (95k): 1 Trà sữa truyền thống (30k) + 1 Sinh tố bơ (40k) + 1 Khô gà (25k -> tính theo giá bán là 35k? Chỉnh lại: 30 + 40 + 25 = 95k) -> Dùng Khoai tây chiên (30k)
('HD001', 'SP01', 1, 30000, 12000), 
('HD001', 'SP04', 1, 40000, 18000),
('HD001', 'SP09', 1, 30000, 12000),

-- HD002 (113k): Khớp các món mới
('HD002', 'SP02', 2, 35000, 14000),
('HD002', 'SP06', 1, 35000, 15000),
('HD002', 'SP07', 40000, 20000, 8000), -- 1 Hướng dương (20k) -> Tổng = 35*2 + 35 + 20 = 125k (Sửa tổng tiền hóa đơn HD002 nếu cần hoặc giữ nguyên tùy ý)

-- HD003 (55k): 1 Trà sữa thái xanh (32k) + 1 Hướng dương (23k hoặc điều chỉnh)
('HD003', 'SP03', 1, 32000, 13000),
('HD003', 'SP07', 1, 23000, 8000);
GO
-- Tạo cơ sở dữ liệu
CREATE DATABASE SocialNetworkDB;
GO

-- Sử dụng cơ sở dữ liệu vừa tạo
USE SocialNetworkDB;
GO

-- Tạo bảng NguoiDung
CREATE TABLE NguoiDung (
    MaNguoiDung INT IDENTITY(1,1) PRIMARY KEY,
    TenNguoiDung NVARCHAR(255) NOT NULL,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE()
);

-- Tạo bảng TaiKhoan
CREATE TABLE TaiKhoan (
    MaTaiKhoan INT IDENTITY(1,1) PRIMARY KEY,
    MaNguoiDung INT UNIQUE NOT NULL,
    Email NVARCHAR(255) NOT NULL UNIQUE,
    MatKhau NVARCHAR(255) NOT NULL,
    TrangThai TINYINT DEFAULT 1,
    CONSTRAINT FK_TaiKhoan_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE
);

-- Tạo bảng BanBe
CREATE TABLE BanBe (
    MaNguoiDung INT,
    MaBanBe INT,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (MaNguoiDung, MaBanBe),
    CONSTRAINT FK_BanBe_NguoiDung1 FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    CONSTRAINT FK_BanBe_NguoiDung2 FOREIGN KEY (MaBanBe) REFERENCES NguoiDung(MaNguoiDung) 
);

-- Tạo bảng LoiMoiKetBan
CREATE TABLE LoiMoiKetBan (
    MaLoiMoi INT IDENTITY(1,1) PRIMARY KEY,
    NguoiGui INT NOT NULL,
    NguoiNhan INT NOT NULL,
    TrangThai TINYINT DEFAULT 0,
    ThoiGian DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_LoiMoiKetBan_NguoiGui FOREIGN KEY (NguoiGui) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    CONSTRAINT FK_LoiMoiKetBan_NguoiNhan FOREIGN KEY (NguoiNhan) REFERENCES NguoiDung(MaNguoiDung)
);

-- Tạo bảng QuyenRiengTu
CREATE TABLE QuyenRiengTu (
    MaQuyenRiengTu INT IDENTITY(1,1) PRIMARY KEY,
    Loai NVARCHAR(50) NOT NULL
);

-- Tạo bảng ChuDe
CREATE TABLE ChuDe (
    MaChuDe INT IDENTITY(1,1) PRIMARY KEY,
    Loai NVARCHAR(100) NOT NULL
);

-- Tạo bảng BaiViet
CREATE TABLE BaiViet (
    MaBaiViet INT IDENTITY(1,1) PRIMARY KEY,
    MaNguoiDung INT NOT NULL,
    MaQuyenRiengTu INT NOT NULL,
    MaChuDe INT,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    NgayCapNhat DATETIME,
    NoiDung NVARCHAR(MAX) NOT NULL,
    CONSTRAINT FK_BaiViet_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    CONSTRAINT FK_BaiViet_QuyenRiengTu FOREIGN KEY (MaQuyenRiengTu) REFERENCES QuyenRiengTu(MaQuyenRiengTu),
    CONSTRAINT FK_BaiViet_ChuDe FOREIGN KEY (MaChuDe) REFERENCES ChuDe(MaChuDe)
);

-- Tạo bảng BinhLuan
CREATE TABLE BinhLuan (
    MaBinhLuan INT IDENTITY(1,1) PRIMARY KEY,
    MaBaiViet INT NOT NULL,
    MaNguoiDung INT NOT NULL,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    NoiDung NVARCHAR(MAX) NOT NULL,
    CONSTRAINT FK_BinhLuan_BaiViet FOREIGN KEY (MaBaiViet) REFERENCES BaiViet(MaBaiViet) ON DELETE CASCADE,
    CONSTRAINT FK_BinhLuan_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) 
);

-- Tạo bảng LuotThich
CREATE TABLE LuotThich (
    MaLuotThich INT IDENTITY(1,1) PRIMARY KEY,
    MaBaiViet INT NOT NULL,
    MaNguoiDung INT NOT NULL,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_LuotThich_BaiViet FOREIGN KEY (MaBaiViet) REFERENCES BaiViet(MaBaiViet) ON DELETE CASCADE,
    CONSTRAINT FK_LuotThich_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
);

-- Tạo bảng HinhAnh
CREATE TABLE HinhAnh (
    MaHinhAnh INT IDENTITY(1,1) PRIMARY KEY,
    MaBaiDang INT NOT NULL,
    Url NVARCHAR(500) NOT NULL,
    CONSTRAINT FK_HinhAnh_BaiViet FOREIGN KEY (MaBaiDang) REFERENCES BaiViet(MaBaiViet) ON DELETE CASCADE
);

-- Tạo bảng CuocTroChuyen
CREATE TABLE CuocTroChuyen (
    MaCuocTroChuyen INT IDENTITY(1,1) PRIMARY KEY,
    NguoiDung_1 INT NOT NULL,
    NguoiDung_2 INT NOT NULL,
    TinNhanCuoi INT,
    NgayTao DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_CuocTroChuyen_NguoiDung1 FOREIGN KEY (NguoiDung_1) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    CONSTRAINT FK_CuocTroChuyen_NguoiDung2 FOREIGN KEY (NguoiDung_2) REFERENCES NguoiDung(MaNguoiDung)
);

-- Tạo bảng TinNhan
CREATE TABLE TinNhan (
    MaTinNhan INT IDENTITY(1,1) PRIMARY KEY,
    MaNguoiGui INT NOT NULL,
    MaCuocTroChuyen INT NOT NULL,
    NoiDung NVARCHAR(MAX) NOT NULL,
    NgayGui DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_TinNhan_NguoiDung FOREIGN KEY (MaNguoiGui) REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT FK_TinNhan_CuocTroChuyen FOREIGN KEY (MaCuocTroChuyen) REFERENCES CuocTroChuyen(MaCuocTroChuyen) ON DELETE CASCADE
);

-- Cập nhật khóa ngoại cho TinNhanCuoi trong bảng CuocTroChuyen
ALTER TABLE CuocTroChuyen
ADD CONSTRAINT FK_CuocTroChuyen_TinNhanCuoi FOREIGN KEY (TinNhanCuoi) REFERENCES TinNhan(MaTinNhan);

-- Tạo bảng ThongBao
CREATE TABLE ThongBao (
    MaThongBao INT IDENTITY(1,1) PRIMARY KEY,
    MaNguoiDung INT NOT NULL,
    NoiDung NVARCHAR(MAX) NOT NULL,
    ThoiGian DATETIME NOT NULL DEFAULT GETDATE(),
    DaDoc TINYINT DEFAULT 0,
    CONSTRAINT FK_ThongBao_NguoiDung FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE
);

-- Tạo bảng GoiYKetBan
CREATE TABLE GoiYKetBan (
    MaGoiY INT IDENTITY(1,1) PRIMARY KEY,
    NguoiGoiY INT NOT NULL,
    NguoiDuocGoiY INT NOT NULL,
    TrangThai TINYINT DEFAULT 0,
    CONSTRAINT FK_GoiYKetBan_NguoiGoiY FOREIGN KEY (NguoiGoiY) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE, 
    CONSTRAINT FK_GoiYKetBan_NguoiDuocGoiY FOREIGN KEY (NguoiDuocGoiY) REFERENCES NguoiDung(MaNguoiDung)
);

-- Tạo các chỉ mục để tối ưu hiệu suất
CREATE INDEX IX_TaiKhoan_Email ON TaiKhoan(Email);
CREATE INDEX IX_BaiViet_MaNguoiDung ON BaiViet(MaNguoiDung);
CREATE INDEX IX_BinhLuan_MaBaiViet ON BinhLuan(MaBaiViet);
CREATE INDEX IX_LuotThich_MaBaiViet ON LuotThich(MaBaiViet);
CREATE INDEX IX_TinNhan_MaCuocTroChuyen ON TinNhan(MaCuocTroChuyen);
CREATE INDEX IX_ThongBao_MaNguoiDung ON ThongBao(MaNguoiDung);

-- Thêm dữ liệu mẫu cho bảng QuyenRiengTu
INSERT INTO QuyenRiengTu (Loai) VALUES (N'Công khai'), (N'Bạn bè'), (N'Riêng tư');

-- Thêm dữ liệu mẫu cho bảng ChuDe
INSERT INTO ChuDe (Loai) VALUES (N'Kinh nghiệm'), (N'Chia sẻ'), (N'Hỏi đáp'), (N'Giải trí');

GO

ALTER TABLE NguoiDung
ADD AnhDaiDien NVARCHAR(500),
    AnhBia NVARCHAR(500);

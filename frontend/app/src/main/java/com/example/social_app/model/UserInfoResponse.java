package com.example.social_app.model;

public class UserInfoResponse {

    private String TenNguoiDung;
    private int MaNguoiDung;
    private String NgayTao;
    private String AnhDaiDien;
    private String AnhBia;
    private String Email;
    private int TrangThai;
    private int TheoDoi;

    // Constructor
    public UserInfoResponse(String TenNguoiDung, int MaNguoiDung, String NgayTao, String AnhDaiDien, String AnhBia, String Email, int TrangThai, int TheoDoi) {
        this.TenNguoiDung = TenNguoiDung;
        this.MaNguoiDung = MaNguoiDung;
        this.NgayTao = NgayTao;
        this.AnhDaiDien = AnhDaiDien;
        this.AnhBia = AnhBia;
        this.Email = Email;
        this.TrangThai = TrangThai;
        this.TheoDoi = TheoDoi;
    }

    public String getTenNguoiDung() {
        return TenNguoiDung;
    }

    public void setTenNguoiDung(String tenNguoiDung) {
        TenNguoiDung = tenNguoiDung;
    }

    public int getMaNguoiDung() {
        return MaNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        MaNguoiDung = maNguoiDung;
    }

    public String getNgayTao() {
        return NgayTao;
    }

    public void setNgayTao(String ngayTao) {
        NgayTao = ngayTao;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
    public String getAnhDaiDien() {
        return AnhDaiDien;
    }

    public void setAnhDaiDien(String anhDaiDien) {
        AnhDaiDien = anhDaiDien;
    }

    public String getAnhBia() {
        return AnhBia;
    }

    public void setAnhBia(String anhBia) {
        AnhBia = anhBia;
    }

    public int getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(int theoDoi) {
        TheoDoi = theoDoi;
    }
    public int getTheoDoi() {
        return TheoDoi;
    }

    public void setTheoDoi(int theoDoi) {
        TheoDoi = theoDoi;
    }
}

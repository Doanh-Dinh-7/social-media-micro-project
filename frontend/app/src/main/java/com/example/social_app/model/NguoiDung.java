package com.example.social_app.model;

public class NguoiDung {
    private String TenNguoiDung;
    private int MaNguoiDung;
    private String NgayTao;
    private String AnhDaiDien;
    private String AnhBia;

    public NguoiDung(String TenNguoiDung, int MaNguoiDung, String NgayTao, String AnhDaiDien, String AnhBia) {
        this.TenNguoiDung = TenNguoiDung;
        this.MaNguoiDung = MaNguoiDung;
        this.NgayTao = NgayTao;
        this.AnhDaiDien = AnhDaiDien;
        this.AnhBia = AnhBia;
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
}

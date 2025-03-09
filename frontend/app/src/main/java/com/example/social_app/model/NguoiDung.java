package com.example.social_app.model;

public class NguoiDung {
    private int maNguoiDung;
    private String tenNguoiDung;
    private String ngayTao;

    public NguoiDung(int maNguoiDung, String tenNguoiDung, String ngayTao) {
        this.maNguoiDung = maNguoiDung;
        this.tenNguoiDung = tenNguoiDung;
        this.ngayTao = ngayTao;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getTenNguoiDung() {
        return tenNguoiDung;
    }

    public void setTenNguoiDung(String tenNguoiDung) {
        this.tenNguoiDung = tenNguoiDung;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}

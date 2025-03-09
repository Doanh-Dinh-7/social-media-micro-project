package com.example.social_app.model;

public class TaiKhoan {
    private int maTaiKhoan;
    private int maNguoiDung;
    private String email;
    private String matKhau;
    private int trangThai;

    public TaiKhoan(int maTaiKhoan, int maNguoiDung, String email, String matKhau, int trangThai) {
        this.maTaiKhoan = maTaiKhoan;
        this.maNguoiDung = maNguoiDung;
        this.email = email;
        this.matKhau = matKhau;
        this.trangThai = trangThai;
    }

    public int getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(int maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }
}

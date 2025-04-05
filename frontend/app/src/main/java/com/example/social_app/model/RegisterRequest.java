package com.example.social_app.model;
//
public class RegisterRequest {
    private String TenNguoiDung;
    private String Email;
    private String MatKhau;

    public RegisterRequest(String tenNguoiDung, String email, String matKhau) {
        this.TenNguoiDung = tenNguoiDung;
        this.Email = email;
        this.MatKhau = matKhau;
    }

    public String getTenNguoiDung() {
        return TenNguoiDung;
    }

    public String getEmail() {
        return Email;
    }

    public String getMatKhau() {
        return MatKhau;
    }
}


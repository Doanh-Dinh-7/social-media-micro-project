package com.example.social_app.model;

public class LoginRequest {
    private String Email;
    private String MatKhau;

    public LoginRequest(String email, String matKhau) {
        this.Email = email;
        this.MatKhau = matKhau;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getMatKhau() {
        return MatKhau;
    }

    public void setMatKhau(String matKhau) {
        this.MatKhau = matKhau;
    }
}

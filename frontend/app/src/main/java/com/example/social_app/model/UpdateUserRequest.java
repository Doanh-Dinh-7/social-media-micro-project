package com.example.social_app.model;

public class UpdateUserRequest {
    private String ten_nguoi_dung;

    public UpdateUserRequest(String ten_nguoi_dung) {
        this.ten_nguoi_dung = ten_nguoi_dung;
    }

    public String getTen_nguoi_dung() {
        return ten_nguoi_dung;
    }

    public void setTen_nguoi_dung(String ten_nguoi_dung) {
        this.ten_nguoi_dung = ten_nguoi_dung;
    }
}

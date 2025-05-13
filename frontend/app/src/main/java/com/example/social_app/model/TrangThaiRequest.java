package com.example.social_app.model;

public class TrangThaiRequest {
    private int TrangThai;

    public TrangThaiRequest(int trangThai) {
        this.TrangThai = trangThai;
    }

    public int getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(int trangThai) {
        this.TrangThai = trangThai;
    }
}
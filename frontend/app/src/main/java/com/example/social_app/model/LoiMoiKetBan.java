package com.example.social_app.model;

public class LoiMoiKetBan {
    private int maLoiMoi;
    private int nguoiGui;
    private int nguoiNhan;
    private int trangThai;
    private String thoiGian;

    public LoiMoiKetBan(int maLoiMoi, int nguoiGui, int nguoiNhan, int trangThai, String thoiGian) {
        this.maLoiMoi = maLoiMoi;
        this.nguoiGui = nguoiGui;
        this.nguoiNhan = nguoiNhan;
        this.trangThai = trangThai;
        this.thoiGian = thoiGian;
    }

    public int getMaLoiMoi() {
        return maLoiMoi;
    }

    public void setMaLoiMoi(int maLoiMoi) {
        this.maLoiMoi = maLoiMoi;
    }

    public int getNguoiGui() {
        return nguoiGui;
    }

    public void setNguoiGui(int nguoiGui) {
        this.nguoiGui = nguoiGui;
    }

    public int getNguoiNhan() {
        return nguoiNhan;
    }

    public void setNguoiNhan(int nguoiNhan) {
        this.nguoiNhan = nguoiNhan;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }
}

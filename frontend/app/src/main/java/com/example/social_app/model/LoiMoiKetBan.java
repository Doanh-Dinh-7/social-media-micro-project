package com.example.social_app.model;

public class LoiMoiKetBan {
    private int MaLoiMoi;
    private int NguoiGui;
    private int NguoiNhan;
    private int TrangThai;
    private String ThoiGian;
    private NguoiDung nguoi_gui;
    private NguoiDung nguoi_nhan;

    public int getMaLoiMoi() {
        return MaLoiMoi;
    }
    public void setMaLoiMoi(int maLoiMoi) {
        MaLoiMoi = maLoiMoi;
    }
    public int getNguoiGui() {
        return NguoiGui;
    }
    public void setNguoiGui(int nguoiGui) {
        NguoiGui = nguoiGui;
    }
    public int getNguoiNhan() {
        return NguoiNhan;
    }
    public void setNguoiNhan(int nguoiNhan) {
        NguoiNhan = nguoiNhan;
    }
    public int getTrangThai() {
        return TrangThai;
    }
    public void setTrangThai(int trangThai) {
        TrangThai = trangThai;
    }
    public String getThoiGian() {
        return ThoiGian;
    }
    public void setThoiGian(String thoiGian) {
        ThoiGian = thoiGian;
    }
    public NguoiDung getNguoi_gui() {
        return nguoi_gui;
    }
    public void setNguoi_gui(NguoiDung nguoi_gui) {
        this.nguoi_gui = nguoi_gui;
    }
    public NguoiDung getNguoi_nhan() {
        return nguoi_nhan;
    }
    public void setNguoi_nhan(NguoiDung nguoi_nhan) {
        this.nguoi_nhan = nguoi_nhan;
    }
}

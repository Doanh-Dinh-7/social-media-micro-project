package com.example.social_app.model;

public class MessageResponse {
    private String NoiDung;
    private int MaTinNhan;
    private int MaNguoiGui;
    private int MaCuocTroChuyen;
    private String NgayGui;
    private NguoiDung nguoi_gui;

    // Constructor
    public MessageResponse(String noiDung, int maTinNhan, int maNguoiGui, int maCuocTroChuyen, String ngayGui, NguoiDung nguoi_gui) {
        this.NoiDung = noiDung;
        this.MaTinNhan = maTinNhan;
        this.MaNguoiGui = maNguoiGui;
        this.MaCuocTroChuyen = maCuocTroChuyen;
        this.NgayGui = ngayGui;
        this.nguoi_gui = nguoi_gui;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public int getMaTinNhan() {
        return MaTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        MaTinNhan = maTinNhan;
    }

    public int getMaNguoiGui() {
        return MaNguoiGui;
    }

    public void setMaNguoiGui(int maNguoiGui) {
        MaNguoiGui = maNguoiGui;
    }

    public int getMaCuocTroChuyen() {
        return MaCuocTroChuyen;
    }

    public void setMaCuocTroChuyen(int maCuocTroChuyen) {
        MaCuocTroChuyen = maCuocTroChuyen;
    }

    public String getNgayGui() {
        return NgayGui;
    }

    public void setNgayGui(String ngayGui) {
        NgayGui = ngayGui;
    }

    public NguoiDung getNguoi_gui() {
        return nguoi_gui;
    }

    public void setNguoi_gui(NguoiDung nguoi_gui) {
        this.nguoi_gui = nguoi_gui;
    }
}


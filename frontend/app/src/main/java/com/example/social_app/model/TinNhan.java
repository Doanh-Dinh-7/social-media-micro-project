package com.example.social_app.model;

public class TinNhan {
    private int maTinNhan;
    private int maNguoiGui;
    private int maCuocTroChuyen;
    private String noiDung;
    private String ngayGui;

    public TinNhan() {
    }

    public TinNhan(int maTinNhan, int maNguoiGui, int maCuocTroChuyen, String noiDung, String ngayGui) {
        this.maTinNhan = maTinNhan;
        this.maNguoiGui = maNguoiGui;
        this.maCuocTroChuyen = maCuocTroChuyen;
        this.noiDung = noiDung;
        this.ngayGui = ngayGui;
    }

    public int getMaTinNhan() {
        return maTinNhan;
    }

    public void setMaTinNhan(int maTinNhan) {
        this.maTinNhan = maTinNhan;
    }

    public int getMaNguoiGui() {
        return maNguoiGui;
    }

    public void setMaNguoiGui(int maNguoiGui) {
        this.maNguoiGui = maNguoiGui;
    }

    public int getMaCuocTroChuyen() {
        return maCuocTroChuyen;
    }

    public void setMaCuocTroChuyen(int maCuocTroChuyen) {
        this.maCuocTroChuyen = maCuocTroChuyen;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getNgayGui() {
        return ngayGui;
    }

    public void setNgayGui(String ngayGui) {
        this.ngayGui = ngayGui;
    }
}

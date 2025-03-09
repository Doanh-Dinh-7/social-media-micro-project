package com.example.social_app.model;

public class ThongBao {
    private int maThongBao;
    private int maNguoiDung;
    private String noiDung;
    private String thoiGian;
    private boolean daDoc;

    public ThongBao() {
    }

    public ThongBao(int maThongBao, int maNguoiDung, String noiDung, String thoiGian, boolean daDoc) {
        this.maThongBao = maThongBao;
        this.maNguoiDung = maNguoiDung;
        this.noiDung = noiDung;
        this.thoiGian = thoiGian;
        this.daDoc = daDoc;
    }

    public int getMaThongBao() {
        return maThongBao;
    }

    public void setMaThongBao(int maThongBao) {
        this.maThongBao = maThongBao;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public String getThoiGian() {
        return thoiGian;
    }

    public void setThoiGian(String thoiGian) {
        this.thoiGian = thoiGian;
    }

    public boolean isDaDoc() {
        return daDoc;
    }

    public void setDaDoc(boolean daDoc) {
        this.daDoc = daDoc;
    }}

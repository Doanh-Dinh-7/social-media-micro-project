package com.example.social_app.model;

public class ThongBao {
    private int maThongBao;
    private int maNguoiDung;

    private String NoiDung;
    private String ThoiGian;
    private boolean daDoc;
    private int maBaiViet;
    private String AnhDaiDien;

    public ThongBao() {
    }

    public ThongBao(int maThongBao, int maNguoiDung, String NoiDung, String ThoiGian, boolean daDoc, int maBaiViet, String AnhDaiDien) {
        this.maThongBao = maThongBao;
        this.maNguoiDung = maNguoiDung;
        this.NoiDung = NoiDung;
        this.ThoiGian = ThoiGian;
        this.daDoc = daDoc;
        this.maBaiViet = maBaiViet;
        this.AnhDaiDien = AnhDaiDien;
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
        return NoiDung;
    }

    public void setNoiDung(String NoiDung) {
        this.NoiDung = NoiDung;
    }

    public String getThoiGian() {
        return ThoiGian;
    }

    public void setThoiGian(String ThoiGian) {
        this.ThoiGian = ThoiGian;
    }

    public boolean isDaDoc() {
        return daDoc;
    }

    public void setDaDoc(boolean daDoc) {
        this.daDoc = daDoc;
    }

    public int getMaBaiViet() {
        return maBaiViet;
    }

    public void setMaBaiViet(int maBaiViet) {
        this.maBaiViet = maBaiViet;
    }

    public String getAnhDaiDien() {
        return AnhDaiDien;
    }

    public void setAnhDaiDien(String AnhDaiDien) {
        this.AnhDaiDien = AnhDaiDien;
    }}

package com.example.social_app.model;

import java.sql.Date;

public class LuotThich {
    private int maLuotThich;
    private int maBaiViet;
    private int maNguoiDung;
    private Date ngayTao;

    public LuotThich(int maLuotThich, int maBaiViet, int maNguoiDung, Date ngayTao) {
        this.maLuotThich = maLuotThich;
        this.maBaiViet = maBaiViet;
        this.maNguoiDung = maNguoiDung;
        this.ngayTao = ngayTao;
    }

    public int getMaLuotThich() {
        return maLuotThich;
    }

    public void setMaLuotThich(int maLuotThich) {
        this.maLuotThich = maLuotThich;
    }

    public int getMaBaiViet() {
        return maBaiViet;
    }

    public void setMaBaiViet(int maBaiViet) {
        this.maBaiViet = maBaiViet;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }
}

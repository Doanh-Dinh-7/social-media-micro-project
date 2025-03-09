package com.example.social_app.model;

import java.sql.Date;

public class BinhLuan {
    private int maBinhLuan;
    private int maBaiViet;
    private int maNguoiDung;
    private Date ngayTao;
    private String noiDung;

    public BinhLuan(int maBinhLuan, int maBaiViet, int maNguoiDung, Date ngayTao, String noiDung) {
        this.maBinhLuan = maBinhLuan;
        this.maBaiViet = maBaiViet;
        this.maNguoiDung = maNguoiDung;
        this.ngayTao = ngayTao;
        this.noiDung = noiDung;
    }

    public int getMaBinhLuan() {
        return maBinhLuan;
    }

    public void setMaBinhLuan(int maBinhLuan) {
        this.maBinhLuan = maBinhLuan;
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

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}

package com.example.social_app.model;

public class BanBe {
    private int maNguoiDung;
    private int maBanBe;
    private String ngayTao;

    public BanBe(int maNguoiDung, int maBanBe, String ngayTao) {
        this.maNguoiDung = maNguoiDung;
        this.maBanBe = maBanBe;
        this.ngayTao = ngayTao;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public int getMaBanBe() {
        return maBanBe;
    }

    public void setMaBanBe(int maBanBe) {
        this.maBanBe = maBanBe;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}

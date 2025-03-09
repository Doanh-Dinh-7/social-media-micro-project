package com.example.social_app.model;

public class CuocTroChuyen {
    private int maCuocTroChuyen;
    private int nguoiDung1;
    private int nguoiDung2;
    private Integer tinNhanCuoi;
    private String ngayTao;

    public CuocTroChuyen() {
    }

    public CuocTroChuyen(int maCuocTroChuyen, int nguoiDung1, int nguoiDung2, Integer tinNhanCuoi, String ngayTao) {
        this.maCuocTroChuyen = maCuocTroChuyen;
        this.nguoiDung1 = nguoiDung1;
        this.nguoiDung2 = nguoiDung2;
        this.tinNhanCuoi = tinNhanCuoi;
        this.ngayTao = ngayTao;
    }

    public int getMaCuocTroChuyen() {
        return maCuocTroChuyen;
    }

    public void setMaCuocTroChuyen(int maCuocTroChuyen) {
        this.maCuocTroChuyen = maCuocTroChuyen;
    }

    public int getNguoiDung1() {
        return nguoiDung1;
    }

    public void setNguoiDung1(int nguoiDung1) {
        this.nguoiDung1 = nguoiDung1;
    }

    public int getNguoiDung2() {
        return nguoiDung2;
    }

    public void setNguoiDung2(int nguoiDung2) {
        this.nguoiDung2 = nguoiDung2;
    }

    public Integer getTinNhanCuoi() {
        return tinNhanCuoi;
    }

    public void setTinNhanCuoi(Integer tinNhanCuoi) {
        this.tinNhanCuoi = tinNhanCuoi;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}

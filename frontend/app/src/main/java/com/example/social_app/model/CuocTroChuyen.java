package com.example.social_app.model;

public class CuocTroChuyen {
    private int MaCuocTroChuyen;
    private int user_id;
    private String ten_nguoi_dung;
    private String anh_dai_dien;
    private String noi_dung_cuoi;
    private String thoi_gian_cuoi;

    // Getter và Setter

    public int getMaCuocTroChuyen() {
        return MaCuocTroChuyen;
    }

    public void setMaCuocTroChuyen(int maCuocTroChuyen) {
        MaCuocTroChuyen = maCuocTroChuyen;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTen_nguoi_dung() {
        return ten_nguoi_dung;
    }

    public void setTen_nguoi_dung(String ten_nguoi_dung) {
        this.ten_nguoi_dung = ten_nguoi_dung;
    }

    public String getAnh_dai_dien() {
        return anh_dai_dien;
    }

    public void setAnh_dai_dien(String anh_dai_dien) {
        this.anh_dai_dien = anh_dai_dien;
    }

    public String getNoi_dung_cuoi() {
        return noi_dung_cuoi;
    }

    public void setNoi_dung_cuoi(String noi_dung_cuoi) {
        this.noi_dung_cuoi = noi_dung_cuoi;
    }

    public String getThoi_gian_cuoi() {
        return thoi_gian_cuoi;
    }

    public void setThoi_gian_cuoi(String thoi_gian_cuoi) {
        this.thoi_gian_cuoi = thoi_gian_cuoi;
    }
}


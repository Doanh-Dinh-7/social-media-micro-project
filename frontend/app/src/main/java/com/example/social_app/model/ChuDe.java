package com.example.social_app.model;

public class ChuDe {
    private int maChuDe;
    private String loai;

    public ChuDe(int maChuDe, String loai) {
        this.maChuDe = maChuDe;
        this.loai = loai;
    }

    public int getMaChuDe() {
        return maChuDe;
    }

    public void setMaChuDe(int maChuDe) {
        this.maChuDe = maChuDe;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }
}

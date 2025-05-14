package com.example.social_app.model;

public class HinhAnh {
    private int MaHinhAnh;
    private int MaBaiDang;
    private String Url;
    public HinhAnh() {}

    public HinhAnh(int MaHinhAnh, int MaBaiDang, String Url) {
        this.MaHinhAnh = MaHinhAnh;
        this.MaBaiDang = MaBaiDang;
        this.Url = Url;
    }

    public int getMaHinhAnh() {
        return MaHinhAnh;
    }

    public void setMaHinhAnh(int MaHinhAnh) {
        this.MaHinhAnh = MaHinhAnh;
    }

    public int getMaBaiDang() {
        return MaBaiDang;
    }

    public void setMaBaiDang(int MaBaiDang) {
        this.MaBaiDang = MaBaiDang;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        this.Url = Url;
    }
}

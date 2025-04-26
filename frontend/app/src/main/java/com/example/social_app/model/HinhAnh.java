package com.example.social_app.model;

import com.google.gson.annotations.SerializedName;

public class HinhAnh {

    @SerializedName("MaHinhAnh")
    private int maHinhAnh;

    @SerializedName("MaBaiDang")
    private int maBaiDang;

    @SerializedName("Url")
    private String url;

    public HinhAnh() {}

    public HinhAnh(int maHinhAnh, int maBaiDang, String url) {
        this.maHinhAnh = maHinhAnh;
        this.maBaiDang = maBaiDang;
        this.url = url;
    }

    public int getMaHinhAnh() {
        return maHinhAnh;
    }

    public void setMaHinhAnh(int maHinhAnh) {
        this.maHinhAnh = maHinhAnh;
    }

    public int getMaBaiDang() {
        return maBaiDang;
    }

    public void setMaBaiDang(int maBaiDang) {
        this.maBaiDang = maBaiDang;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

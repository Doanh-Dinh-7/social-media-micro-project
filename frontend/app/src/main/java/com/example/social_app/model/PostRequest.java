package com.example.social_app.model;

import java.util.List;

public class PostRequest {
    private String noi_dung;
    private int ma_quyen_rieng_tu;
    private Integer ma_chu_de;
    private List<String> images;

    // Constructor
    public PostRequest(String noi_dung, int ma_quyen_rieng_tu, Integer ma_chu_de, List<String> images) {
        this.noi_dung = noi_dung;
        this.ma_quyen_rieng_tu = ma_quyen_rieng_tu;
        this.ma_chu_de = ma_chu_de;
        this.images = images;
    }

    // Getter và Setter
    public String getNoi_dung() {
        return noi_dung;
    }

    public void setNoi_dung(String noi_dung) {
        this.noi_dung = noi_dung;
    }

    public int getMa_quyen_rieng_tu() {
        return ma_quyen_rieng_tu;
    }

    public void setMa_quyen_rieng_tu(int ma_quyen_rieng_tu) {
        this.ma_quyen_rieng_tu = ma_quyen_rieng_tu;
    }

    public Integer getMa_chu_de() {
        return ma_chu_de;
    }

    public void setMa_chu_de(Integer ma_chu_de) {
        this.ma_chu_de = ma_chu_de;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}

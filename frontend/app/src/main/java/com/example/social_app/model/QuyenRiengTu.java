package com.example.social_app.model;

public class QuyenRiengTu {
    private int maQuyenRiengTu;
    private String loai;

    public QuyenRiengTu(int maQuyenRiengTu, String loai) {
        this.maQuyenRiengTu = maQuyenRiengTu;
        this.loai = loai;
    }

    public int getMaQuyenRiengTu() {
        return maQuyenRiengTu;
    }

    public void setMaQuyenRiengTu(int maQuyenRiengTu) {
        this.maQuyenRiengTu = maQuyenRiengTu;
    }

    public String getLoai() {
        return loai;
    }

    public void setLoai(String loai) {
        this.loai = loai;
    }
}

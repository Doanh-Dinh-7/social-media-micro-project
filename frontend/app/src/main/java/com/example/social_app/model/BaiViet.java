package com.example.social_app.model;

import java.sql.Date;

public class BaiViet {
    private int maBaiViet;
    private int maNguoiDung;
    private int maQuyenRiengTu;
    private int maChuDe;
    private Date ngayTao;
    private Date ngayCapNhat;
    private String noiDung;

    public BaiViet(int maBaiViet, int maNguoiDung, int maQuyenRiengTu, int maChuDe, Date ngayTao, Date ngayCapNhat, String noiDung) {
        this.maBaiViet = maBaiViet;
        this.maNguoiDung = maNguoiDung;
        this.maQuyenRiengTu = maQuyenRiengTu;
        this.maChuDe = maChuDe;
        this.ngayTao = ngayTao;
        this.ngayCapNhat = ngayCapNhat;
        this.noiDung = noiDung;
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

    public int getMaQuyenRiengTu() {
        return maQuyenRiengTu;
    }

    public void setMaQuyenRiengTu(int maQuyenRiengTu) {
        this.maQuyenRiengTu = maQuyenRiengTu;
    }

    public int getMaChuDe() {
        return maChuDe;
    }

    public void setMaChuDe(int maChuDe) {
        this.maChuDe = maChuDe;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public Date getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(Date ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }
}

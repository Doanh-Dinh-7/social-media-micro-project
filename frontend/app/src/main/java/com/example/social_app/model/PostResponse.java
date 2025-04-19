package com.example.social_app.model;

import java.io.Serializable;
import java.util.List;

public class PostResponse  implements Serializable {

    private String NoiDung;
    private int MaQuyenRiengTu;
    private int MaChuDe;
    private int MaBaiViet;
    private int MaNguoiDung;
    private String NgayTao;
    private String NgayCapNhat;
    private NguoiDung nguoi_dung;
    private List<HinhAnh> hinh_anh;
    private int so_luot_thich;
    private int so_binh_luan;
    private boolean da_thich;

    // Getters and Setters
    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public int getMaQuyenRiengTu() {
        return MaQuyenRiengTu;
    }

    public void setMaQuyenRiengTu(int maQuyenRiengTu) {
        MaQuyenRiengTu = maQuyenRiengTu;
    }

    public int getMaChuDe() {
        return MaChuDe;
    }

    public void setMaChuDe(int maChuDe) {
        MaChuDe = maChuDe;
    }

    public int getMaBaiViet() {
        return MaBaiViet;
    }

    public void setMaBaiViet(int maBaiViet) {
        MaBaiViet = maBaiViet;
    }

    public int getMaNguoiDung() {
        return MaNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        MaNguoiDung = maNguoiDung;
    }

    public String getNgayTao() {
        return NgayTao;
    }

    public void setNgayTao(String ngayTao) {
        NgayTao = ngayTao;
    }

    public String getNgayCapNhat() {
        return NgayCapNhat;
    }

    public void setNgayCapNhat(String ngayCapNhat) {
        NgayCapNhat = ngayCapNhat;
    }

    public NguoiDung getNguoi_dung() {
        return nguoi_dung;
    }

    public void setNguoi_dung(NguoiDung nguoi_dung) {
        this.nguoi_dung = nguoi_dung;
    }

    public List<HinhAnh> getHinh_anh() {
        return hinh_anh;
    }

    public void setHinh_anh(List<HinhAnh> hinh_anh) {
        this.hinh_anh = hinh_anh;
    }

    public int getSo_luot_thich() {
        return so_luot_thich;
    }

    public void setSo_luot_thich(int so_luot_thich) {
        this.so_luot_thich = so_luot_thich;
    }

    public int getSo_binh_luan() {
        return so_binh_luan;
    }

    public void setSo_binh_luan(int so_binh_luan) {
        this.so_binh_luan = so_binh_luan;
    }

    public boolean isDa_thich() {
        return da_thich;
    }

    public void setDa_thich(boolean da_thich) {
        this.da_thich = da_thich;
    }

    // Inner class for NguoiDung
    public static class NguoiDung {
        private String TenNguoiDung;
        private int MaNguoiDung;
        private String NgayTao;

        // Getters and Setters
        public String getTenNguoiDung() {
            return TenNguoiDung;
        }

        public void setTenNguoiDung(String tenNguoiDung) {
            TenNguoiDung = tenNguoiDung;
        }

        public int getMaNguoiDung() {
            return MaNguoiDung;
        }

        public void setMaNguoiDung(int maNguoiDung) {
            MaNguoiDung = maNguoiDung;
        }

        public String getNgayTao() {
            return NgayTao;
        }

        public void setNgayTao(String ngayTao) {
            NgayTao = ngayTao;
        }
    }
}


package com.example.social_app.model;


public class CommentResponse {
    private String NoiDung;  // Nội dung bình luận
    private int MaBinhLuan;  // ID của bình luận
    private int MaBaiViet;   // ID bài viết
    private int MaNguoiDung; // ID người dùng
    private String NgayTao;  // Ngày tạo bình luận
    private String TenNguoiDung; // Tên người dùng

    // Constructor, getters, setters

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public int getMaBinhLuan() {
        return MaBinhLuan;
    }

    public void setMaBinhLuan(int maBinhLuan) {
        MaBinhLuan = maBinhLuan;
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

    public String getTenNguoiDung() {
        return TenNguoiDung;
    }

    public void setTenNguoiDung(String tenNguoiDung) {
        TenNguoiDung = tenNguoiDung;
    }
}



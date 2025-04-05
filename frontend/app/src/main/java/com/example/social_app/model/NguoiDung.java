package com.example.social_app.model;
//
//public class NguoiDung {
//    private int maNguoiDung;
//    private String tenNguoiDung;
//    private String ngayTao;
//
//    public NguoiDung( String tenNguoiDung,int maNguoiDung, String ngayTao) {
//        this.tenNguoiDung = tenNguoiDung;
//        this.maNguoiDung = maNguoiDung;
//        this.ngayTao = ngayTao;
//    }
//
//    public int getMaNguoiDung() {
//        return maNguoiDung;
//    }
//
//    public void setMaNguoiDung(int maNguoiDung) {
//        this.maNguoiDung = maNguoiDung;
//    }
//
//    public String getTenNguoiDung() {
//        return tenNguoiDung;
//    }
//
//    public void setTenNguoiDung(String tenNguoiDung) {
//        this.tenNguoiDung = tenNguoiDung;
//    }
//
//    public String getNgayTao() {
//        return ngayTao;
//    }
//
//    public void setNgayTao(String ngayTao) {
//        this.ngayTao = ngayTao;
//    }
//}

public class NguoiDung {
    private String tenNguoiDung;
    private int maNguoiDung;
    private String ngayTao;

    // Constructor mặc định (không cần phải khởi tạo maNguoiDung khi đăng ký)
    public NguoiDung(String tenNguoiDung, int maNguoiDung, String ngayTao) {
        this.tenNguoiDung = tenNguoiDung;
        this.maNguoiDung = maNguoiDung;
        this.ngayTao = ngayTao;
    }

    // Getter và Setter
    public String getTenNguoiDung() {
        return tenNguoiDung;
    }

    public void setTenNguoiDung(String tenNguoiDung) {
        this.tenNguoiDung = tenNguoiDung;
    }

    public int getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(int maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
    }
}

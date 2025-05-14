package com.example.social_app.model;

public class MessageRequest {
    private String NoiDung;
    private int MaCuocTroChuyen;
    private int NguoiNhan;

    public MessageRequest(String noiDung, int maCuocTroChuyen, int nguoiNhan) {
        this.NoiDung = noiDung;
        this.MaCuocTroChuyen = maCuocTroChuyen;
        this.NguoiNhan = nguoiNhan;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        this.NoiDung = noiDung;
    }

    public int getMaCuocTroChuyen() {
        return MaCuocTroChuyen;
    }

    public void setMaCuocTroChuyen(int maCuocTroChuyen) {
        this.MaCuocTroChuyen = maCuocTroChuyen;
    }

    public int getNguoiNhan() {
        return NguoiNhan;
    }

    public void setNguoiNhan(int nguoiNhan) {
        this.NguoiNhan = nguoiNhan;
    }
}
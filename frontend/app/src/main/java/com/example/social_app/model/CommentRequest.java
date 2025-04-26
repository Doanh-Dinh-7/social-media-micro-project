package com.example.social_app.model;

public class CommentRequest {
    private String NoiDung;
    private int MaBaiViet;

    public CommentRequest(String noiDung, int maBaiViet) {
        this.NoiDung = noiDung;
        this.MaBaiViet = maBaiViet;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public int getMaBaiViet() {
        return MaBaiViet;
    }

    public void setMaBaiViet(int maBaiViet) {
        MaBaiViet = maBaiViet;
    }
}


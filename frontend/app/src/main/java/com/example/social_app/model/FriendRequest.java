package com.example.social_app.model;

public class FriendRequest {
    private int NguoiNhan;

    public FriendRequest(int nguoiNhan) {
        this.NguoiNhan = nguoiNhan;
    }
    public int getNguoiNhan() {
        return NguoiNhan;
    }
    public void setNguoiNhan(int nguoiNhan) {
        this.NguoiNhan = nguoiNhan;
    }
}

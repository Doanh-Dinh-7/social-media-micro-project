package com.example.social_app.model;

public class UserInfoRequest {
    private int user_id;

    // Constructor
    public UserInfoRequest(int user_id) {
        this.user_id = user_id;
    }

    // Getter and Setter
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

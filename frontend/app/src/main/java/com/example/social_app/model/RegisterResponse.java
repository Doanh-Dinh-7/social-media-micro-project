package com.example.social_app.model;

import com.example.social_app.model.NguoiDung;

public class RegisterResponse {
    private String access_token;
    private String refresh_token;
    private String token_type;
    private NguoiDung user;

    public String getAccessToken() {
        return access_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public String getTokenType() {
        return token_type;
    }

    public NguoiDung getUser() {
        return user;
    }
}

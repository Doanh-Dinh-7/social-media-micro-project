package com.example.social_app.network;

import com.example.social_app.model.AuthResponse;
import com.example.social_app.model.BaiViet;
import com.example.social_app.model.NguoiDung;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("auth/register")
    Call<AuthResponse> register(@Body NguoiDung user);

    @POST("auth/login")
    Call<AuthResponse> login(@Body NguoiDung user);

    Call<List<BaiViet>> getPosts();
}

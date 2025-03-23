package com.example.social_app.network;

import com.example.social_app.model.LoginRequest;
import com.example.social_app.model.LoginResponse;
import com.example.social_app.model.RegisterRequest;
import com.example.social_app.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
}

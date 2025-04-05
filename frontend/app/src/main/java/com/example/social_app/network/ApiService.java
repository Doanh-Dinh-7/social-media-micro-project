package com.example.social_app.network;

import com.example.social_app.model.LoginRequest;
import com.example.social_app.model.LoginResponse;
import com.example.social_app.model.PostRequest;
import com.example.social_app.model.PostResponse;
import com.example.social_app.model.RegisterRequest;
import com.example.social_app.model.RegisterResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

        @Multipart
        @POST("posts")
        Call<PostResponse> createPostWithImages(
                @Header("Authorization") String authToken,
                @Part("postData") PostRequest postRequest,
                @Part List<MultipartBody.Part> images
        );
    }



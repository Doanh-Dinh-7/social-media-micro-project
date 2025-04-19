package com.example.social_app.network;

import com.example.social_app.model.CloudinaryUploadResult;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudinaryService {
    @Multipart
    @POST("image/upload")
    Call<CloudinaryUploadResult> uploadImage(
            @Part MultipartBody.Part file,
            @Part("upload_preset") RequestBody uploadPreset
    );
}




package com.example.social_app.model;

import com.google.gson.annotations.SerializedName;

public class CloudinaryUploadResult {
    @SerializedName("secure_url")
    private String secureUrl;

    public String getSecureUrl() {
        return secureUrl;
    }
}

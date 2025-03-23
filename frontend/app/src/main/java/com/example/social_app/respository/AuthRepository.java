//package com.example.social_app.respository;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.social_app.model.AuthResponse;
//import com.example.social_app.model.NguoiDung;
//import com.example.social_app.network.API;
//import com.example.social_app.network.RetrofitClient;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class AuthRepository {
//    private API api;
//
//    public AuthRepository() {
//        api = RetrofitClient.getClient().create(API.class);
//    }
//
//    public LiveData<AuthResponse> register(NguoiDung user) {
//        MutableLiveData<AuthResponse> authLiveData = new MutableLiveData<>();
//        api.register(user).enqueue(new Callback<AuthResponse>() {
//            @Override
//            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
//                if (response.isSuccessful()) {
//                    authLiveData.setValue(response.body());
//                } else {
//                    authLiveData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AuthResponse> call, Throwable t) {
//                authLiveData.setValue(null);
//            }
//        });
//        return authLiveData;
//    }
//
//    public LiveData<AuthResponse> login(NguoiDung user) {
//        MutableLiveData<AuthResponse> authLiveData = new MutableLiveData<>();
//        api.login(user).enqueue(new Callback<AuthResponse>() {
//            @Override
//            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
//                if (response.isSuccessful()) {
//                    authLiveData.setValue(response.body());
//                } else {
//                    authLiveData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<AuthResponse> call, Throwable t) {
//                authLiveData.setValue(null);
//            }
//        });
//        return authLiveData;
//    }
//}

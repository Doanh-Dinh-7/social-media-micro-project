//package com.example.social_app.respository;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.social_app.model.BaiViet;
//import com.example.social_app.network.API;
//import com.example.social_app.network.RetrofitClient;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class PostRepository {
//    private API api;
//
//    public PostRepository() {
//        api = RetrofitClient.getClient().create(API.class);
//    }
//
//    public LiveData<List<BaiViet>> getPosts() {
//        MutableLiveData<List<BaiViet>> postsLiveData = new MutableLiveData<>();
//
//        api.getPosts().enqueue(new Callback<List<BaiViet>>() {
//            @Override
//            public void onResponse(Call<List<BaiViet>> call, Response<List<BaiViet>> response) {
//                if (response.isSuccessful()) {
//                    postsLiveData.setValue(response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<BaiViet>> call, Throwable t) {
//                postsLiveData.setValue(null);
//            }
//        });
//
//        return postsLiveData;
//    }
//}

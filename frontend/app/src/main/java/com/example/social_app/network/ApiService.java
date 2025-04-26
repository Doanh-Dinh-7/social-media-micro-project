package com.example.social_app.network;

import com.example.social_app.model.ChuDe;
import com.example.social_app.model.CommentRequest;
import com.example.social_app.model.CommentResponse;
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
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

//    @Multipart
//    @POST("posts")
//    Call<PostResponse> createPost(
//            @Header("Authorization") String authToken,
//            @Part("noi_dung") RequestBody noiDung,
//            @Part("ma_quyen_rieng_tu") RequestBody maQuyenRiengTu,
//            @Part("ma_chu_de") RequestBody maChuDe,
//            @Part List<MultipartBody.Part> images
//        );
//    }

//    @POST("posts")
//    Call<PostResponse> createPost(
//            @Header("Authorization") String token,
//            @Body PostRequest postRequest
//    );

//    @FormUrlEncoded
//    @POST("posts")
//    Call<PostResponse> createPost(
//            @Header("Authorization") String authToken,
//            @Field("noi_dung") String noiDung,
//            @Field("ma_quyen_rieng_tu") int maQuyenRiengTu,
//            @Field("ma_chu_de") Integer maChuDe,
//            @Field("images[]") List<String> images
//    );

    @Multipart
    @POST("posts")
    Call<PostResponse> createPost(
            @Header("Authorization") String authToken,
            @Part("noi_dung") RequestBody noiDung,
            @Part("ma_quyen_rieng_tu") RequestBody maQuyenRiengTu,
            @Part("ma_chu_de") RequestBody maChuDe, // Nếu có
            @Part List<MultipartBody.Part> images // Danh sách ảnh (nếu có)
    );

    @GET("posts")
    Call<List<PostResponse>> getAllPosts(@Header("Authorization") String authToken);

    @POST("posts/{ma_bai_viet}/luot-thich")
    Call<Void> likePost(@Header("Authorization") String authToken, @Path("ma_bai_viet") int postId);

    @DELETE("posts/{ma_bai_viet}/luot-thich")
    Call<Void> unlikePost(@Header("Authorization") String authToken, @Path("ma_bai_viet") int postId);

    @POST("posts/{ma_bai_viet}/binh-luan")
    Call<CommentResponse> createComment(@Header("Authorization") String authToken,
                                            @Path("ma_bai_viet") int postId,
                                            @Body CommentRequest commentRequest);

    @GET("posts/{ma_bai_viet}/binh-luan")
    Call<List<CommentResponse>> getComments(
            @Header("Authorization") String authToken,
            @Path("ma_bai_viet") int postId
    );

    @DELETE("posts/{ma_bai_viet}/binh-luan/{ma_binh_luan}")
    Call<Void> deleteComment(
            @Header("Authorization") String authToken,
            @Path("ma_bai_viet") int postId,
            @Path("ma_binh_luan") int commentId
    );



}







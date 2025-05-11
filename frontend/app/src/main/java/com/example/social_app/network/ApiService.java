package com.example.social_app.network;

import com.example.social_app.model.CommentRequest;
import com.example.social_app.model.CommentResponse;
import com.example.social_app.model.CuocTroChuyen;
import com.example.social_app.model.FriendRequest;
import com.example.social_app.model.FriendResponse;
import com.example.social_app.model.LoginRequest;
import com.example.social_app.model.LoginResponse;
import com.example.social_app.model.LoiMoiKetBan;
import com.example.social_app.model.MessageRequest;
import com.example.social_app.model.MessageResponse;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.model.PostResponse;
import com.example.social_app.model.RegisterRequest;
import com.example.social_app.model.RegisterResponse;
import com.example.social_app.model.ThongBao;
import com.example.social_app.model.UpdateUserRequest;
import com.example.social_app.model.UserInfoResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

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

    @GET("posts/{post_id}")
    Call<PostResponse> getPostById(
            @Header("Authorization") String authToken,
            @Path("post_id") int postId
    );

    @PUT("auth/register/update-name")
    Call<Void> updateUsername(
            @Header("Authorization") String token,
            @Body UpdateUserRequest request
    );

    @GET("users/profile/{user_id}")
    Call<UserInfoResponse> getUserInfo(
            @Header("Authorization") String token,
            @Path("user_id") int userId
    );

    @GET("users/search")
    Call<List<NguoiDung>> searchUsers(
            @Header("Authorization") String authToken,
            @Query("keyword") String keyword);

    @GET("messages/conversations")
    Call<List<CuocTroChuyen>> getConversations(@Header("Authorization") String authToken);

    @GET("messages/conversations/search")
    Call<List<CuocTroChuyen>> searchConversations(
            @Header("Authorization") String authToken,
            @Query("keyword") String keyword);

    @POST("messages")
    Call<MessageResponse> sendMessage(
            @Header("Authorization") String authToken,
            @Body MessageRequest messageRequest
    );

    @GET("messages/{user_id}")
    Call<List<MessageResponse>> getMessages(
            @Header("Authorization") String authToken,
            @Path("user_id") int userId
    );

    @Multipart
    @PUT("users/avatar")
    Call<NguoiDung> updateProfilePicture(
            @Header("Authorization") String token,
            @Part MultipartBody.Part profileImage
    );

    @Multipart
    @PUT("users/cover")
    Call<NguoiDung> updateCoverPicture(
            @Header("Authorization") String authToken,
            @Part MultipartBody.Part coverImage
    );

    @GET("friends")
    Call<List<FriendResponse>> getDanhSachBanBe(@Header("Authorization") String authHeader, @Query("userId") int userId);

    @POST("friends/requests")
    Call<LoiMoiKetBan> sendFriendRequest(
            @Header("Authorization") String token,
            @Body FriendRequest body
    );

    @GET("friends/requests")
    Call<List<LoiMoiKetBan>> getFriendRequest(@Header("Authorization") String token);

    @GET("notifications")
    Call<List<ThongBao>> getThongBaoBaiViet(@Header("Authorization") String token);

    @DELETE("friends/unfriend/{friend_id}")
    Call<Void> huyKetBan(@Header("Authorization") String token, @Path("friend_id") int friendId);

    @DELETE("friends/requests/{ma_loi_moi}")
    Call<LoiMoiKetBan> cancelFriendRequest(
            @Header("Authorization") String token,
            @Path("ma_loi_moi") int MaLoiMoiId
    );

}



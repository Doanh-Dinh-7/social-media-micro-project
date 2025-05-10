package com.example.social_app.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.MessageRequest;
import com.example.social_app.model.MessageResponse;
import com.example.social_app.model.UserInfoResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.MessageAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MessageFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText edtMessage;
    private ImageView btnSend;
    private MessageAdapter messageAdapter;
    private List<MessageResponse> messageList = new ArrayList<>();
    private ApiService apiService;
    private String authToken;
    private int userId;
    private String tenNguoiDung;
    private int currentUserId = -1;
    private int conversationId = 0;
    private ImageView btnBack;
    private TextView txtName;
    private ImageView imgAvatar;

    public MessageFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        rvMessages = view.findViewById(R.id.rvMessages);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);
        txtName = view.findViewById(R.id.txtName);
        imgAvatar = view.findViewById(R.id.imgAvatar);

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        if (getArguments() != null) {
            userId = getArguments().getInt("user_id", -1);
            conversationId = getArguments().getInt("conversation_id", 0);
            tenNguoiDung = getArguments().getString("ten_nguoi_dung", "");
            currentUserId = getArguments().getInt("current_user_id", -1);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        rvMessages.setAdapter(messageAdapter);

        if (userId == -1 || TextUtils.isEmpty(authToken)) {
            Toast.makeText(getContext(), "Dữ liệu người dùng không hợp lệ", Toast.LENGTH_SHORT).show();
            return view;
        }

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loadMessages();
        getUserInfo(userId, authToken);

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void getUserInfo(int userId, String token) {
        Call<UserInfoResponse> call = apiService.getUserInfo("Bearer " + token, userId);
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtName.setText(response.body().getTenNguoiDung());
                    String avatarUrl = response.body().getAnhDaiDien();

                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(avatarUrl)
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.mipmap.user_img);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                txtName.setText("Lỗi khi lấy thông tin người dùng");
            }
        });
    }

    private void loadMessages() {
        apiService.getMessages("Bearer " + authToken, userId).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    messageAdapter.notifyDataSetChanged();
                    Collections.reverse(messageList);
                    rvMessages.scrollToPosition(messageList.size() - 1);

                    if (!messageList.isEmpty()) {
                        conversationId = messageList.get(0).getMaCuocTroChuyen();
                        Log.d("MESSAGE", "Conversation ID: " + conversationId);
                    }
                } else {
                    Toast.makeText(getContext(), "Không thể tải tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        String content = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        if (conversationId == 0) {
            Toast.makeText(getContext(), "Chưa có cuộc trò chuyện", Toast.LENGTH_SHORT).show();
            return;
        }

        MessageRequest messageRequest = new MessageRequest(content, conversationId, userId);

        apiService.sendMessage("Bearer " + authToken, messageRequest).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.add(response.body());
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    rvMessages.scrollToPosition(messageList.size() - 1);
                    edtMessage.setText("");
                } else {
                    Toast.makeText(getContext(), "Không thể gửi tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

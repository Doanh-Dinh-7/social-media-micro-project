package com.example.social_app.view.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.social_app.model.MessageResponse;
import com.example.social_app.model.MessageToSend;
import com.example.social_app.model.UserInfoResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.MessageAdapter;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;

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
    private int currentUserId = -1;
    private int conversationId = 0;
    private TextView txtName;
    private ImageView imgAvatar, btnBack;

    private WebSocket webSocket;
    private final Gson gson = new Gson();

    private static final String SOCKET_BASE_URL = "ws://172.40.172.247:8000/api/messages/";

    public MessageFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        rvMessages = view.findViewById(R.id.rvMessages);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        txtName = view.findViewById(R.id.txtName);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");
        currentUserId = sharedPref.getInt("user_id", -1);

        if (getArguments() != null) {
            userId = getArguments().getInt("user_id", -1);
            conversationId = getArguments().getInt("conversation_id", 0);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvMessages.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(messageList, currentUserId);
        rvMessages.setAdapter(messageAdapter);

        apiService = RetrofitClient.getClient().create(ApiService.class);

        loadMessages();
        getUserInfo(userId, authToken);
        initWebSocket();

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void getUserInfo(int userId, String token) {
        Call<UserInfoResponse> call = apiService.getUserInfo("Bearer " + token, userId);
        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, retrofit2.Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtName.setText(response.body().getTenNguoiDung());
                    String avatarUrl = response.body().getAnhDaiDien();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(getContext()).load(avatarUrl).into(imgAvatar);
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
            public void onResponse(Call<List<MessageResponse>> call, retrofit2.Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageList.clear();
                    messageList.addAll(response.body());
                    messageAdapter.notifyDataSetChanged();
                    rvMessages.scrollToPosition(messageList.size() - 1);

                    if (!messageList.isEmpty()) {
                        conversationId = messageList.get(0).getMaCuocTroChuyen();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initWebSocket() {
        OkHttpClient client = new OkHttpClient();
        String socketUrl = SOCKET_BASE_URL + currentUserId;
        Request request = new Request.Builder()
                .url(socketUrl)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("WebSocket", "Kết nối thành công");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "Nhận tin nhắn: " + text);
                MessageResponse message = gson.fromJson(text, MessageResponse.class);

                if (message.getMaNguoiGui() == currentUserId) return;

                requireActivity().runOnUiThread(() -> {
                    messageList.add(message);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    rvMessages.scrollToPosition(messageList.size() - 1);
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("WebSocket", "Lỗi kết nối: " + t.getMessage());
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Lỗi kết nối WebSocket", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d("WebSocket", "Đã đóng: " + reason);
            }
        });
    }

    private void sendMessage() {
        String content = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(content)) return;

        if (webSocket == null) {
            Toast.makeText(getContext(), "WebSocket chưa kết nối", Toast.LENGTH_SHORT).show();
            return;
        }

        MessageToSend messageToSend = new MessageToSend(content, userId);
        String messageJson = gson.toJson(messageToSend);
        webSocket.send(messageJson);

        String timestamp = getCurrentTimestamp();
        MessageResponse displayMessage = new MessageResponse(content, 0, currentUserId, conversationId, timestamp, null);  // Placeholder for sender

        messageList.add(displayMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMessages.scrollToPosition(messageList.size() - 1);

        edtMessage.setText("");
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(System.currentTimeMillis());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webSocket != null) {
            webSocket.close(1000, "Fragment dừng");
        }
    }
}

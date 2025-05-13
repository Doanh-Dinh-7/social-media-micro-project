package com.example.social_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.FriendRequest;
import com.example.social_app.model.LoiMoiKetBan;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.model.SuggestionsResponse;
import com.example.social_app.model.TrangThaiRequest;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.LoiMoiAdapter;
import com.example.social_app.view.adapters.SuggestionAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThongBaoKetBanFragment extends Fragment {

    private RecyclerView recyclerView, recyclerSuggestions;
    private LoiMoiAdapter adapter;
    private SuggestionAdapter suggestionAdapter;
    private String authToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thongbao_ketban, container, false);

        recyclerView = view.findViewById(R.id.recyclerInvites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerSuggestions = view.findViewById(R.id.recyclerSuggestions);
        recyclerSuggestions.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Lấy authToken từ SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("auth_token", null);

        // Kiểm tra nếu không có authToken
        if (authToken == null) {
            Toast.makeText(getContext(), "Token không hợp lệ, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Lấy dữ liệu từ API
        getLoiMoiKetBanFromApi();
        getFriendSuggestions();

        return view;
    }

    // Lấy danh sách lời mời kết bạn từ API
    private void getLoiMoiKetBanFromApi() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<LoiMoiKetBan>> call = apiService.getFriendRequest("Bearer " + authToken);

        call.enqueue(new Callback<List<LoiMoiKetBan>>() {
            @Override
            public void onResponse(Call<List<LoiMoiKetBan>> call, Response<List<LoiMoiKetBan>> response) {
                if (response.isSuccessful()) {
                    List<LoiMoiKetBan> loiMoiList = response.body();
                    if (loiMoiList != null && !loiMoiList.isEmpty()) {
                        adapter = new LoiMoiAdapter(getContext(), loiMoiList, new LoiMoiAdapter.OnActionListener() {
                            @Override
                            public void onAccept(int maLoiMoi) {
                                acceptFriendRequest(maLoiMoi);
                            }

                            @Override
                            public void onDelete(int maLoiMoi) {
                                deleteFriendRequest(maLoiMoi);
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Không có lời mời kết bạn", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<LoiMoiKetBan>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Lấy danh sách gợi ý kết bạn
    // Gắn với phần cũ bạn đưa ở trên, giữ nguyên import...

    private void getFriendSuggestions() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<SuggestionsResponse> call = apiService.getFriendSuggestions("Bearer " + authToken);

        call.enqueue(new Callback<SuggestionsResponse>() {
            @Override
            public void onResponse(Call<SuggestionsResponse> call, Response<SuggestionsResponse> response) {
                if (response.isSuccessful()) {
                    SuggestionsResponse suggestionsResponse = response.body();
                    if (suggestionsResponse != null && suggestionsResponse.getSuggestions() != null &&
                            !suggestionsResponse.getSuggestions().isEmpty()) {
                        suggestionAdapter = new SuggestionAdapter(getContext(), suggestionsResponse.getSuggestions(),
                                new SuggestionAdapter.OnActionListener() {
                                    @Override
                                    public void onAccept(NguoiDung nguoiDung) {
                                        if (nguoiDung.getQuanHe() == 2) {
                                            cancelFriendRequest(nguoiDung); // đã gửi rồi thì hủy
                                        } else {
                                            sendFriendRequest(nguoiDung); // chưa gửi thì gửi
                                        }
                                    }
                                });
                        recyclerSuggestions.setAdapter(suggestionAdapter);
                    } else {
                        Toast.makeText(getContext(), "Không có đề xuất kết bạn", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu đề xuất", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SuggestionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendFriendRequest(NguoiDung nguoiDung) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        FriendRequest request = new FriendRequest(nguoiDung.getMaNguoiDung());

        apiService.sendFriendRequest("Bearer " + authToken, request).enqueue(new Callback<LoiMoiKetBan>() {
            @Override
            public void onResponse(Call<LoiMoiKetBan> call, Response<LoiMoiKetBan> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoiMoiKetBan loiMoi = response.body();
                    int maLoiMoi = loiMoi.getMaLoiMoi();

                    SharedPreferences preferences = getContext().getSharedPreferences("FriendRequests", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("maLoiMoi_" + nguoiDung.getMaNguoiDung(), maLoiMoi);
                    editor.apply();

                    suggestionAdapter.updateUserStatus(nguoiDung, 2); // 2: đã gửi lời mời
                    Toast.makeText(getContext(), "Đã gửi lời mời đến " + nguoiDung.getTenNguoiDung(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không thể gửi lời mời", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoiMoiKetBan> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối khi gửi lời mời", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelFriendRequest(NguoiDung nguoiDung) {
        SharedPreferences preferences = getContext().getSharedPreferences("FriendRequests", Context.MODE_PRIVATE);
        int maLoiMoi = preferences.getInt("maLoiMoi_" + nguoiDung.getMaNguoiDung(), -1);

        if (maLoiMoi != -1) {
            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            apiService.cancelFriendRequest("Bearer " + authToken, maLoiMoi)
                    .enqueue(new Callback<LoiMoiKetBan>() {
                        @Override
                        public void onResponse(Call<LoiMoiKetBan> call, Response<LoiMoiKetBan> response) {
                            if (response.isSuccessful()) {
                                suggestionAdapter.updateUserStatus(nguoiDung, 4); // 4: chưa kết bạn
                                Toast.makeText(getContext(), "Đã hủy lời mời kết bạn", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Không thể hủy lời mời", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoiMoiKetBan> call, Throwable t) {
                            Toast.makeText(getContext(), "Lỗi kết nối khi hủy lời mời", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Không tìm thấy mã lời mời", Toast.LENGTH_SHORT).show();
        }
    }

    private void acceptFriendRequest(int maLoiMoi) {
        TrangThaiRequest request = new TrangThaiRequest(1); // 1 là đồng ý
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.respondToFriendRequest(maLoiMoi, request, "Bearer " + authToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeLoiMoiById(maLoiMoi);
                    Toast.makeText(getContext(), "Đã chấp nhận lời mời", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không thể chấp nhận lời mời", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFriendRequest(int maLoiMoi) {
        TrangThaiRequest request = new TrangThaiRequest(0);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.respondToFriendRequest(maLoiMoi, request, "Bearer " + authToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    adapter.removeLoiMoiById(maLoiMoi);
                    Toast.makeText(getContext(), "Đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Không thể từ chối lời mời", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
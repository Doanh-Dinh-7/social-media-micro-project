package com.example.social_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.LoiMoiKetBan;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.LoiMoiAdapter;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThongBaoKetBanFragment extends Fragment {

    private RecyclerView recyclerView;
    private LoiMoiAdapter adapter;
    private String authToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thongbao_ketban, container, false);

        recyclerView = view.findViewById(R.id.recyclerInvites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                        // Hiển thị dữ liệu vào RecyclerView
                        adapter = new LoiMoiAdapter(getContext(), loiMoiList, new LoiMoiAdapter.OnActionListener() {
                            @Override
                            public void onAccept(int maLoiMoi) {
                                // Xử lý khi người dùng chấp nhận lời mời
                                Toast.makeText(getContext(), "Chấp nhận lời mời " + maLoiMoi, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onDelete(int maLoiMoi) {
                                // Xử lý khi người dùng xóa lời mời
                                Toast.makeText(getContext(), "Xóa lời mời " + maLoiMoi, Toast.LENGTH_SHORT).show();
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
}
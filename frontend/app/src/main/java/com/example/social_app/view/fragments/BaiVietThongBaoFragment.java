package com.example.social_app.view.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.ThongBao;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.ThongBaoAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaiVietThongBaoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ThongBaoAdapter adapter;
    private List<ThongBao> thongBaoList = new ArrayList<>();
    private String authToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baiviet_notifications, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewThongBao);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ThongBaoAdapter(thongBaoList);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        fetchThongBaos();

        return view;
    }

    private void fetchThongBaos() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<ThongBao>> call = apiService.getThongBaoBaiViet("Bearer " + authToken);

        call.enqueue(new Callback<List<ThongBao>>() {
            @Override
            public void onResponse(Call<List<ThongBao>> call, Response<List<ThongBao>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    thongBaoList.clear();
                    thongBaoList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Không thể tải thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ThongBao>> call, Throwable t) {
                Log.e("BaiVietThongBao", "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
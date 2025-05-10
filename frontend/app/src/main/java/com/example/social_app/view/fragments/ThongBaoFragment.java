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
import androidx.viewpager2.widget.ViewPager2;

import com.example.social_app.R;
import com.example.social_app.model.ThongBao;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.adapters.ThongBaoAdapter;
import com.example.social_app.view.adapters.ThongBaoPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThongBaoFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ThongBaoAdapter adapter;
    private List<ThongBao> thongBaoList = new ArrayList<>();
    private String authToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Ánh xạ TabLayout và ViewPager2
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Set adapter cho ViewPager2
        ThongBaoPagerAdapter pagerAdapter = new ThongBaoPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        // Liên kết TabLayout và ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Bài viết");
            } else {
                tab.setText("Kết bạn");
            }
        }).attach();

        // Lấy token từ SharedPreferences
        SharedPreferences sharedPref = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        authToken = sharedPref.getString("auth_token", "");

        return view;
    }
}

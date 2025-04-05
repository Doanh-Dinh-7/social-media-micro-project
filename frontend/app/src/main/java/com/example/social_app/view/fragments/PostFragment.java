package com.example.social_app.view.fragments;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import com.example.social_app.R;
//import com.google.android.material.tabs.TabLayout;
//
//public class PostFragment extends Fragment {
//
//    private TabLayout tabLayout;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_home, container, false);
//
//        // Ánh xạ TabLayout
//        tabLayout = view.findViewById(R.id.tabLayout);
//
//        if (tabLayout != null) {
//            tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
//            tabLayout.addTab(tabLayout.newTab().setText("Kinh nghiệm"));
//        } else {
//            Log.e("PostFragment", "tabLayout is null");
//        }
//
//        return view;
//    }
//}


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PostFragment extends Fragment {

    private TabLayout tabLayout;
    private BottomNavigationView bottomNavigationView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ TabLayout
        tabLayout = view.findViewById(R.id.tabLayout);

        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
            tabLayout.addTab(tabLayout.newTab().setText("Kinh nghiệm"));
        } else {
            Log.e("PostFragment", "tabLayout is null");
        }
        bottomNavigationView = view.findViewById(R.id.bottomNavigationView);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;


            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new PostFragment();
            } else if (item.getItemId() == R.id.nav_post) {
                selectedFragment = new CreatePostFragment();
            }

            if (selectedFragment != null) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        return view;
    }
}

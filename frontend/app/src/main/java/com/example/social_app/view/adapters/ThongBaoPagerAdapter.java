package com.example.social_app.view.adapters;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.social_app.view.fragments.BaiVietThongBaoFragment;
import com.example.social_app.view.fragments.ThongBaoKetBanFragment;


import androidx.fragment.app.FragmentActivity;

public class ThongBaoPagerAdapter extends FragmentStateAdapter {

    public ThongBaoPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new BaiVietThongBaoFragment();
        } else {
            return new ThongBaoKetBanFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
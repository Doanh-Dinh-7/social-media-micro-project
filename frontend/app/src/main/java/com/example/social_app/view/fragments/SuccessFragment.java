package com.example.social_app.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.social_app.R;
import com.example.social_app.view.activities.LoginActivity;
import com.example.social_app.view.activities.RegisterActivity;

public class SuccessFragment extends Fragment {
    private Button btnStart;
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_success, container, false);

        btnStart = view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(v -> {
            String email = getArguments() != null ? getArguments().getString("EMAIL", ""): "";
            ((RegisterActivity) getActivity()).goToUserInfor(email);});

        return view;
    }
}
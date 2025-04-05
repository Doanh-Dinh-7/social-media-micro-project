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

public class LoginOptionsFragment extends Fragment {
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_options, container, false);
        Button btnLogin = view.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> ((LoginActivity) getActivity()).goToLoginForm());
//        btnLogin.setOnClickListener(v -> ((LoginActivity) getActivity()).goToPost());

        TextView txtRegister = view.findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });
        return view;
    }
}
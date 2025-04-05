package com.example.social_app.view.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.social_app.R;
import com.example.social_app.view.activities.LoginActivity;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.activities.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFormFragment extends Fragment {
    private TextInputEditText edtEmail, edtPassword;
    private Button btnNext;
    private ImageButton btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);

        btnBack = view.findViewById(R.id.btn_Back);

        btnBack.setOnClickListener(v -> ((LoginActivity) getActivity()).goToLoginOptions());

        TextView txtRegister = view.findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
        });


        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnNext = view.findViewById(R.id.btn_next);

        btnNext.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (getActivity() instanceof LoginActivity) {
                ((LoginActivity) getActivity()).loginUser(email, password);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
            updateButtonState();}
        };

        edtEmail.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);

        return view;
    }

    public void updateButtonState() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty() && password.length() >= 6) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}


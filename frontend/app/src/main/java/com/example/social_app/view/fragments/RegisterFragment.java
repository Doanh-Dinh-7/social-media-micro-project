package com.example.social_app.view.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.view.activities.LoginActivity;
import com.example.social_app.view.activities.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterFragment extends Fragment {

    private Button btnContinue;
    private ImageButton btnBack;
    private CheckBox cbTerms;
    private TextInputEditText edtEmail, edtPassword, etConfirmPassword;


    // Constructor rỗng (bắt buộc cho Fragment)
    public RegisterFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> ((LoginActivity) getActivity()).goToLoginOptions());

        btnContinue = view.findViewById(R.id.btn_continue);

//        btnContinue.setOnClickListener(v -> ((RegisterActivity) getActivity()).goToSuccess());
        btnContinue.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thêm log để debug
            Log.d("DEBUG", "Email: " + email);
            Log.d("DEBUG", "Password: " + password);


                if (getActivity() instanceof RegisterActivity) {
                    ((RegisterActivity) getActivity()).registerUser(email, password);
                }
        });

//        ((RegisterActivity) getActivity()).registerUser("", edtEmail.toString(), edtPassword.toString()));


        // Ánh xạ view
        btnContinue = view.findViewById(R.id.btn_continue);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        cbTerms = view.findViewById(R.id.cb_terms);
        cbTerms.setButtonTintList(ContextCompat.getColorStateList(requireContext(), R.color.orange));


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        };

        // Gán sự kiện nhập liệu
        edtEmail.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);
        etConfirmPassword.addTextChangedListener(textWatcher);


        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonState());

        return view;
    }
    public void updateButtonState() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        boolean isChecked = cbTerms.isChecked();

        if (!email.isEmpty() && !password.isEmpty() &&  password.length() >= 6 && !confirmPassword.isEmpty() && isChecked && password.equals(confirmPassword)) {
            btnContinue.setEnabled(true);
        } else {
            btnContinue.setEnabled(false);
        }
    }
}
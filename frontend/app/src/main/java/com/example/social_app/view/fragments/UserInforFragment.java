package com.example.social_app.view.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.model.UpdateUserRequest;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.LoginActivity;
import com.example.social_app.view.activities.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class UserInforFragment extends Fragment {

    private ImageButton btnBack;
    private TextInputEditText etUsername, etDob, etGender;
    private Button btnContinue;
    private String token;

    public UserInforFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_infor, container, false);

        btnBack = view.findViewById(R.id.btn_Back);
        btnContinue = view.findViewById(R.id.btn_continue);
        etUsername = view.findViewById(R.id.et_username);
        etDob = view.findViewById(R.id.et_dob);
        etGender = view.findViewById(R.id.et_gender);

        if (getArguments() != null) {
            token = getArguments().getString("TOKEN", "");
        }

        btnBack.setOnClickListener(v -> ((RegisterActivity) getActivity()).goToSuccess());

        btnContinue.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(getActivity(), "Tên người dùng không được để trống", Toast.LENGTH_SHORT).show();
                return;
            }
            updateUsername(username);

            Toast.makeText(getActivity(), "Xác nhận thành công!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
            getActivity().finish();
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        };

        etUsername.addTextChangedListener(textWatcher);
        etDob.addTextChangedListener(textWatcher);
        etGender.addTextChangedListener(textWatcher);

        etDob.setOnClickListener(v -> showDatePicker());

        etGender.setOnClickListener(v -> showGenderDialog());

        return view;
    }

    private void showGenderDialog() {
        String[] genderOptions = {"Nam", "Nữ"};

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Chọn giới tính")
                .setItems(genderOptions, (dialog, which) -> {
                    etGender.setText(genderOptions[which]);
                })
                .show();
    }

    private void updateButtonState() {
        String username = etUsername.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = etGender.getText().toString().trim();

        if (!username.isEmpty() && !dob.isEmpty() && !gender.isEmpty()) {
            btnContinue.setEnabled(true);
            btnContinue.setText("Xác nhận");
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setText("Tiếp tục");
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, dayOfMonth) -> {
            String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + dayOfMonth;
            etDob.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateUsername(String username) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        UpdateUserRequest request = new UpdateUserRequest(username);

        apiService.updateUsername("Bearer " + token, request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Void> call, @NonNull retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Cập nhật tên người dùng thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra("TOKEN", token);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}

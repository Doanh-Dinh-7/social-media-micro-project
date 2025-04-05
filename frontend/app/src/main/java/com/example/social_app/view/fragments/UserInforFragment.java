package com.example.social_app.view.fragments;

//
//import android.app.DatePickerDialog;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.social_app.R;
//import com.example.social_app.view.activities.RegisterActivity;
//import com.google.android.material.textfield.TextInputEditText;
//
//import java.util.Calendar;
//
//
//public class UserInforFragment extends Fragment {
//    private ImageButton btnBack;
//    private TextInputEditText etUsername, etDob, etGender;
//    private Button btnContinue;
//
//    public UserInforFragment() {
//        // Required empty public constructor
//    }
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_user_infor, container, false);
//
//        btnBack = view.findViewById(R.id.btn_Back);
//        btnBack.setOnClickListener(v -> ((RegisterActivity) getActivity()).goToSuccess());
//
//        btnContinue = view.findViewById(R.id.btn_continue);
//        btnContinue.setOnClickListener(v -> {
//            Toast.makeText(getActivity(), "Xác nhận thành công!", Toast.LENGTH_SHORT).show();
//            updateButtonState();
//        });
//
//        // Ánh xạ view
//        btnContinue = view.findViewById(R.id.btn_continue);
//        etUsername = view.findViewById(R.id.et_username);
//        etDob = view.findViewById(R.id.et_dob);
//        etGender = view.findViewById(R.id.et_gender);
//
//
//
//        TextWatcher textWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                updateButtonState();
//            }
//        };
//
//        // Gán sự kiện nhập liệu
//
//        etUsername.addTextChangedListener(textWatcher);
//        etDob.addTextChangedListener(textWatcher);
//        etGender.addTextChangedListener(textWatcher);
//
//        etDob.setOnClickListener(v -> showDatePicker());
//
//        return view;
//    }
//
//    private void showDatePicker() {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, dayOfMonth) -> {
//            String selectedDate = day + "/" + (month + 1) + "/" + year;
//            etDob.setText(selectedDate);
//        }, year, month, day);
//
//        datePickerDialog.show();
//    }
//
//    private void updateButtonState() {
//        String username = etUsername.getText().toString().trim();
//        String dob = etDob.getText().toString().trim();
//        String gender = etGender.getText().toString().trim();
//
//        if (!username.isEmpty() && !dob.isEmpty() && !gender.isEmpty()) {
//            btnContinue.setEnabled(true);
//            btnContinue.setText("Xác nhận");
//        } else {
//            btnContinue.setEnabled(false);
//            btnContinue.setText("Tiếp tục");
//        }
//    }
//}
//



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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.activities.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class UserInforFragment extends Fragment {

    private ImageButton btnBack;
    private TextInputEditText etUsername, etDob, etGender;
    private Button btnContinue;

    public UserInforFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_infor, container, false);

        // Ánh xạ view
        btnBack = view.findViewById(R.id.btn_Back);
        btnContinue = view.findViewById(R.id.btn_continue);
        etUsername = view.findViewById(R.id.et_username);
        etDob = view.findViewById(R.id.et_dob);
        etGender = view.findViewById(R.id.et_gender);

        // Xử lý sự kiện quay lại
        btnBack.setOnClickListener(v -> ((RegisterActivity) getActivity()).goToSuccess());
        String email = getArguments() != null ? getArguments().getString("EMAIL", ""): "";
        // Xử lý sự kiện nút Tiếp tục
        btnContinue.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Xác nhận thành công!", Toast.LENGTH_SHORT).show();
            updateButtonState();  // Kiểm tra lại trạng thái nút khi nhấn
            Intent intent = new Intent(getActivity(), PostActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        // Gán sự kiện nhập liệu
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

        // Xử lý chọn ngày sinh
        etDob.setOnClickListener(v -> showDatePicker());

        return view;
    }

    // Hiển thị DatePickerDialog để chọn ngày
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, dayOfMonth) -> {
            String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + dayOfMonth;  // Định dạng yyyy-MM-dd
            etDob.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    // Cập nhật trạng thái của nút tiếp tục dựa trên giá trị nhập vào
    private void updateButtonState() {
        String username = etUsername.getText().toString().trim();
        String dob = etDob.getText().toString().trim();
        String gender = etGender.getText().toString().trim();

        // Nếu tất cả các trường đều có giá trị hợp lệ, thì enable nút "Xác nhận"
        if (!username.isEmpty() && !dob.isEmpty() && !gender.isEmpty()) {
            btnContinue.setEnabled(true);
            btnContinue.setText("Xác nhận");
        } else {
            btnContinue.setEnabled(false);
            btnContinue.setText("Tiếp tục");
        }
    }
}

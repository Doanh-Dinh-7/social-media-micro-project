package com.example.social_app.view.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.social_app.R;
import com.example.social_app.network.ApiService;
import com.example.social_app.model.RegisterRequest;
import com.example.social_app.model.RegisterResponse;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.fragments.RegisterFragment;
import com.example.social_app.view.fragments.SuccessFragment;
import com.example.social_app.view.fragments.UserInforFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if (savedInstanceState == null) {
            loadFragment(new RegisterFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void goToSuccess() { loadFragment(new SuccessFragment());}
    public void goToUserInfor(String email) {
        String token = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("TOKEN", "");

        Bundle bundle = new Bundle();
        bundle.putString("EMAIL", email);
        bundle.putString("TOKEN", token);

        UserInforFragment userInforFragment = new UserInforFragment();
        userInforFragment.setArguments(bundle);

        loadFragment(userInforFragment);
    }

    public void registerUser(String email, String matKhau) {
        RegisterRequest request = new RegisterRequest("user", email, matKhau);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("TOKEN", token)
                            .apply();

                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    goToSuccess();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.example.social_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.social_app.R;
import com.example.social_app.model.FriendResponse;
import com.example.social_app.model.NguoiDung;
import com.example.social_app.model.UserInfoResponse;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.FriendListAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendListAdapter adapter;
    private List<FriendResponse> friendList = new ArrayList<>();
    private List<FriendResponse> originalFriendList = new ArrayList<>();
    private ApiService apiService;
    private String authToken;
    private TextView txtSoBanBe, txtName;
    private EditText edtSearch;
    private ImageView btnBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerFriendList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FriendListAdapter(friendList, getContext());
        recyclerView.setAdapter(adapter);
        txtSoBanBe = view.findViewById(R.id.txtSoBanBe);
        txtName = view.findViewById(R.id.txtName);
        edtSearch = view.findViewById(R.id.edt_search);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            ((PostActivity) getActivity()).showBottomNavigationView();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new ProfileFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        adapter.setOnItemClickListener(friend -> {
            int receiverId = friend.getBan().getMaNguoiDung();
            ((PostActivity) getActivity()).hideBottomNavigationView();

            MessageFragment messageFragment = new MessageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", receiverId);
            messageFragment.setArguments(bundle);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, messageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        Bundle args = getArguments();
        if (args != null && args.containsKey("userId")) {
            int userId = args.getInt("userId");

            SharedPreferences sharedPref = getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
            authToken = sharedPref.getString("auth_token", "");

            if (authToken != null && !authToken.isEmpty()) {
                apiService = RetrofitClient.getClient().create(ApiService.class);
                getFriendList(userId);
                getUserInfo(userId, authToken);
            } else {
                Toast.makeText(getContext(), "Không tìm thấy token xác thực", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show();
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                filterFriend(query);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void getFriendList(int userId) {
        String authHeader = "Bearer " + authToken;
        apiService.getDanhSachBanBe(authHeader, userId).enqueue(new Callback<List<FriendResponse>>() {
            @Override
            public void onResponse(Call<List<FriendResponse>> call, Response<List<FriendResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    friendList.clear();
                    friendList.addAll(response.body());
                    originalFriendList.clear();
                    originalFriendList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    txtSoBanBe.setText(String.valueOf(friendList.size()));
            } else {
                    Toast.makeText(getContext(), "Tải danh sách bạn bè thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FriendResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(int userId, String token) {
        apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<UserInfoResponse> call = apiService.getUserInfo("Bearer " + token, userId);

        call.enqueue(new Callback<UserInfoResponse>() {
            @Override
            public void onResponse(Call<UserInfoResponse> call, Response<UserInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String username = response.body().getTenNguoiDung();
                    txtName.setText(username);
                }
            }

            @Override
            public void onFailure(Call<UserInfoResponse> call, Throwable t) {
                txtName.setText("Connection error");
            }
        });
    }

    private void filterFriend(String query) {
        friendList.clear();

        if (query.isEmpty()) {
            friendList.addAll(originalFriendList);
        } else {
            for (FriendResponse friend : originalFriendList) {
                NguoiDung nguoiDung = friend.getBan();
                if (nguoiDung != null &&
                        nguoiDung.getTenNguoiDung() != null &&
                        nguoiDung.getTenNguoiDung().toLowerCase().contains(query.toLowerCase())) {
                    friendList.add(friend);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

}







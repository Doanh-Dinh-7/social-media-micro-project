package com.example.social_app.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.social_app.R;
import com.example.social_app.model.CuocTroChuyen;
import com.example.social_app.network.ApiService;
import com.example.social_app.network.RetrofitClient;
import com.example.social_app.view.activities.PostActivity;
import com.example.social_app.view.adapters.ConversationAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationFragment extends Fragment implements ConversationAdapter.OnConversationClickListener {

    private RecyclerView recyclerView;
    private ConversationAdapter conversationAdapter;
    private List<CuocTroChuyen> conversationList;
    private ApiService apiService;
    private int currentUserId = -1;
    private String authToken;
    private ImageView btnBack;
    private EditText edtSearch;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_conversations);
        tabLayout = view.findViewById(R.id.tabLayout);
        btnBack = view.findViewById(R.id.btnBack);
        edtSearch = view.findViewById(R.id.edt_search);

        conversationList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(conversationList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(conversationAdapter);

        tabLayout.addTab(tabLayout.newTab().setText("Quan trọng"));
        tabLayout.addTab(tabLayout.newTab().setText("Khác"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if ("Quan trọng".equals(tab.getText().toString())) {
                    recyclerView.setVisibility(View.VISIBLE);
                    conversationAdapter.updateList(conversationList);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    conversationAdapter.updateList(new ArrayList<>());
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
            if (getActivity() instanceof PostActivity) {
                ((PostActivity) getActivity()).showBottomNavigationView();
            }
        });

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        authToken = sharedPreferences.getString("auth_token", null);
        currentUserId = sharedPreferences.getInt("user_id", -1);

        if (authToken != null && !authToken.isEmpty()) {
            apiService = RetrofitClient.getClient().create(ApiService.class);
            loadConversationsFromApi();
        } else {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString().trim();
                filterConversations(query);
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadConversationsFromApi() {
        Call<List<CuocTroChuyen>> call = apiService.getConversations("Bearer " + authToken);
        call.enqueue(new Callback<List<CuocTroChuyen>>() {
            @Override
            public void onResponse(Call<List<CuocTroChuyen>> call, Response<List<CuocTroChuyen>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    conversationList.clear();
                    conversationList.addAll(response.body());
                    conversationAdapter.updateList(conversationList);
                } else {
                    Toast.makeText(getContext(), "Không tải được cuộc trò chuyện", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CuocTroChuyen>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterConversations(String query) {
        if (query.isEmpty()) {
            loadConversationsFromApi();
        } else {
            Call<List<CuocTroChuyen>> call = apiService.searchConversations("Bearer " + authToken, query);
            call.enqueue(new Callback<List<CuocTroChuyen>>() {
                @Override
                public void onResponse(Call<List<CuocTroChuyen>> call, Response<List<CuocTroChuyen>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        conversationList.clear();
                        conversationList.addAll(response.body());
                        conversationAdapter.updateList(conversationList);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<CuocTroChuyen>> call, Throwable t) {
                    Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onConversationClick(CuocTroChuyen conversation) {
        MessageFragment messageFragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", conversation.getUser_id());
        args.putString("ten_nguoi_dung", conversation.getTen_nguoi_dung());
        args.putInt("conversation_id", conversation.getMaCuocTroChuyen());
        args.putInt("current_user_id", currentUserId);
        messageFragment.setArguments(args);

        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, messageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

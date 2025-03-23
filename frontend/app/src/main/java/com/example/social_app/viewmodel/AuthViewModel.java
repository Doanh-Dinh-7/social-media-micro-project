//package com.example.social_app.viewmodel;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.social_app.model.AuthResponse;
//import com.example.social_app.model.NguoiDung;
//import com.example.social_app.respository.AuthRepository;
//
//public class AuthViewModel extends ViewModel {
//    private AuthRepository authRepository;
//
//    public AuthViewModel() {
//        authRepository = new AuthRepository();
//    }
//
//    public LiveData<AuthResponse> register(NguoiDung user) {
//        return authRepository.register(user);
//    }
//
//    public LiveData<AuthResponse> login(NguoiDung user) {
//        return authRepository.login(user);
//    }
//}

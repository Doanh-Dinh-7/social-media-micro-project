package com.example.social_app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SuggestionsResponse {
    private List<NguoiDung> suggestions;

    public List<NguoiDung> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<NguoiDung> suggestions) {
        this.suggestions = suggestions;
    }
}
package com.example.social_app.model;

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
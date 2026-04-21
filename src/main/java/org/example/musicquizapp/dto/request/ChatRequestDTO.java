package org.example.musicquizapp.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// FIX: Flyttet ud af SearchRequestDTO til sin egen fil
@Getter
@Setter
public class ChatRequestDTO {

    private String model;
    private List<Message> messages;
    private Integer max_tokens;
    private Double temperature;

    public void setMaxTokens(int maxTokens) {
        this.max_tokens = maxTokens;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
package org.example.musicquizapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// FIX: Flyttet ud af SearchRequestDTO til sin egen fil + Lombok
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String role;
    private String content;
}
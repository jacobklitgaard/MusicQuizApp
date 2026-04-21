package org.example.musicquizapp.dto.response;

import java.util.List;
import lombok.*;

@ToString
@Getter
@Setter
public class QuizQuestionDTO {

    private String question;
    private List<String> options;
    private String correctAnswer;
    private String type;
    private String preview;
    private String trackId;
    private String artistName;  // FIX: tilføjet så frontend kan kalde fun fact API

    public QuizQuestionDTO(String question, List<String> options, String correctAnswer, String type, String preview, String trackId, String artistName) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.type = type;
        this.preview = preview;
        this.trackId = trackId;
        this.artistName = artistName;
    }
}
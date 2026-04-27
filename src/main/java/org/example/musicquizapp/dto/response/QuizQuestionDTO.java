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
    private String artistName;
    private String albumCover;

    public QuizQuestionDTO(String question, List<String> options, String correctAnswer, String type, String preview, String trackId, String artistName, String albumCover) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.type = type;
        this.preview = preview;
        this.trackId = trackId;
        this.artistName = artistName;
        this.albumCover = albumCover;
    }
}
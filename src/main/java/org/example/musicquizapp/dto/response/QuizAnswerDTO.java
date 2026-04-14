package org.example.musicquizapp.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class QuizAnswerDTO {
    private String answer;
    private String trackId;
    private String correctAnswer;
    private String type;
}
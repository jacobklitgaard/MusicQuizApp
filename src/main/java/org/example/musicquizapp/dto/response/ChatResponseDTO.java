package org.example.musicquizapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// FIX: Var tom — nu kan Jackson faktisk deserialisere OpenAI's svar
@Getter
@Setter
public class ChatResponseDTO {

    private List<Choice> choices;

    @Getter
    @Setter
    public static class Choice {
        private Message message;
    }

    @Getter
    @Setter
    public static class Message {
        private String content;
    }
}
package org.example.musicquizapp.client;

import org.example.musicquizapp.dto.request.ChatRequestDTO;
import org.example.musicquizapp.dto.request.Message;
import org.example.musicquizapp.dto.response.ChatResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OpenAiClient {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    public OpenAiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    // Bruges til /quiz/funfact endpoint
    public Mono<String> generateFunFact(String artist) {
        String prompt = "Giv et kort og sjovt fun fact på dansk om musikartisten: " + artist;

        return sendRequest(
                "Du er en musikekspert der giver korte og sjove fun facts på dansk.",
                prompt,
                80,
                0.9
        );
    }

    // Bruges af MusicQuizService til at generere quizspørgsmål
    public Mono<String> generateOpenAiQuestion(String type, String trackName, String artistName, String extra) {
        String prompt = "ARTIST".equals(type)
                ? "Lav et kort quizspørgsmål på dansk, der spørger om kunstneren bag sangen \"" + trackName + "\". Kun selve spørgsmålet, ingen svar."
                : "Lav et kort quizspørgsmål på dansk om sangen \"" + trackName + "\" af " + artistName + ". Kun selve spørgsmålet, ingen svar.";

        return sendRequest(
                "Du er en sjov musikquiz-vært. Stil korte, klare spørgsmål på dansk.",
                prompt,
                60,
                0.8
        );
    }

    // Fælles hjælpemetode så der ikke gentages kode
    private Mono<String> sendRequest(String systemPrompt, String userPrompt, int maxTokens, double temperature) {
        ChatRequestDTO request = new ChatRequestDTO();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(List.of(
                new Message("system", systemPrompt),
                new Message("user", userPrompt)
        ));
        request.setMaxTokens(maxTokens);
        request.setTemperature(temperature);

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBearerAuth(apiKey))
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponseDTO.class)
                .map(res -> res.getChoices().get(0).getMessage().getContent());
    }
}
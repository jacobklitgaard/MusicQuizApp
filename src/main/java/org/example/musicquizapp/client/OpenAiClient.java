package org.example.musicquizapp.client;

import org.example.musicquizapp.dto.response.OpenAiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class OpenAiClient {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    public OpenAiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> generateOpenAiQuestion(String type, String song, String artist, String extra) {

        String prompt = """
                Du laver musik-quiz spørgsmål.

                TYPE: %s
                SONG: %s
                ARTIST: %s
                EXTRA: %s

                Lav 1 kort multiple choice spørgsmål på dansk:
                - 4 svarmuligheder
                - kun 1 korrekt
                - returnér kun spørgsmålet + svarmuligheder
                """.formatted(type, song, artist, extra);

        return webClient.post()
                .uri("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue("""
                {
                  "model": "gpt-4o-mini",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(prompt))
                .retrieve()
                .bodyToMono(OpenAiResponse.class)
                .map(res -> {
                    if (res == null
                            || res.getChoices() == null
                            || res.getChoices().isEmpty()) {
                        return "Hvilken sang er dette?";
                    }

                    return res.getChoices()
                            .get(0)
                            .getMessage()
                            .getContent();
                })
                .onErrorReturn("Hvilken sang er dette?");
    }
}
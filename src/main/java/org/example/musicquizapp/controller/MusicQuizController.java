package org.example.musicquizapp.controller;

import org.example.musicquizapp.client.OpenAiClient;
import org.example.musicquizapp.dto.request.SearchRequestDTO;
import org.example.musicquizapp.dto.response.QuizAnswerDTO;
import org.example.musicquizapp.dto.response.QuizQuestionDTO;
import org.example.musicquizapp.service.MusicQuizService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class MusicQuizController {

    private final MusicQuizService musicQuizService;
    private final OpenAiClient openAiClient; // FIX: injecter i stedet for statisk kald

    public MusicQuizController(MusicQuizService musicQuizService, OpenAiClient openAiClient) {
        this.musicQuizService = musicQuizService;
        this.openAiClient = openAiClient;
    }

    @GetMapping("/test")
    public String test() {
        return "API virker!";
    }

    @PostMapping("/quiz")
    public Mono<QuizQuestionDTO> generateQuiz(@RequestBody SearchRequestDTO request) {
        return musicQuizService.generateQuestion(request);
    }

    @PostMapping("/quiz/funfact")
    public Mono<String> getFunFact(@RequestBody String artistName) {
        return openAiClient.generateFunFact(artistName); // FIX: lille 'o' — instans-kald
    }

    @PostMapping("/quiz/answer")
    public Mono<Boolean> checkAnswer(@RequestBody QuizAnswerDTO answer) {
        return musicQuizService.checkAnswer(answer);
    }
}
package org.example.musicquizapp.controller;

import org.example.musicquizapp.dto.request.SearchRequestDTO;
import org.example.musicquizapp.dto.response.QuizAnswerDTO;
import org.example.musicquizapp.dto.response.QuizQuestionDTO;
import org.example.musicquizapp.service.MusicQuizService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class MusicQuizController {

    private final MusicQuizService musicQuizService;

    public MusicQuizController(MusicQuizService musicQuizService) {
        this.musicQuizService = musicQuizService;
    }

    @GetMapping("/test")
    public String test() {
        return "API virker!";
    }

    @PostMapping("/quiz")
    public Mono<QuizQuestionDTO> generateQuiz(@RequestBody SearchRequestDTO request) {
        return musicQuizService.generateQuestion(request);
    }

    @PostMapping("/quiz/bonus")
    public Mono<QuizQuestionDTO> generateBonus(@RequestBody QuizAnswerDTO answer) {
        return musicQuizService.generateBonusQuestion(answer.getTrackId());
    }

    @PostMapping("/quiz/answer")
    public Mono<Boolean> checkAnswer(@RequestBody QuizAnswerDTO answer) {
        return musicQuizService.checkAnswer(answer);
    }
}

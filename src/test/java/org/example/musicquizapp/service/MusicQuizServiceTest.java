package org.example.musicquizapp.service;

import org.example.musicquizapp.client.DeezerClient;
import org.example.musicquizapp.client.OpenAiClient;
import org.example.musicquizapp.dto.deezer.TrackDTO;
import org.example.musicquizapp.dto.response.QuizAnswerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MusicQuizServiceTest {

    @Mock
    private DeezerClient deezerClient;

    @Mock
    private OpenAiClient openAiClient;

    private MusicQuizService musicQuizService;

    @BeforeEach
    void setUp() {
        musicQuizService = new MusicQuizService(deezerClient, openAiClient);
    }

    // ✅ TEST DER VIRKER
    // Givet et korrekt svar returnerer checkAnswer() true
    @Test
    void checkAnswer_medKorrektSvar_returnerTrue() {
        // Arrange
        var trackId = "123";

        var mockTrack = new TrackDTO();
        mockTrack.setId(trackId);
        mockTrack.setTitle("Bohemian Rhapsody"); // bruger setTitle, ikke setName

        when(deezerClient.getTrack(trackId)).thenReturn(Mono.just(mockTrack));

        var answer = new QuizAnswerDTO();
        answer.setTrackId(trackId);
        answer.setAnswer("Bohemian Rhapsody");

        // Act & Assert
        StepVerifier.create(musicQuizService.checkAnswer(answer))
                .expectNext(true)
                .verifyComplete();
    }

    // ❌ TEST DER FEJLER MED VILJE
    // Forventer true men svaret er forkert – pipelinen skal blive rød
    @Test
    void checkAnswer_medForkertSvar_returnerTrue_FEJLER_MED_VILJE() {
        // Arrange
        var trackId = "123";

        var mockTrack = new TrackDTO();
        mockTrack.setId(trackId);
        mockTrack.setTitle("Bohemian Rhapsody");

        when(deezerClient.getTrack(trackId)).thenReturn(Mono.just(mockTrack));

        var answer = new QuizAnswerDTO();
        answer.setTrackId(trackId);
        answer.setAnswer("Forkert sang"); // forkert svar

        // Act & Assert
        // 💥 Vi forventer true men får false — pipelinen bliver rød
        StepVerifier.create(musicQuizService.checkAnswer(answer))
                .expectNext(false) // ← forkert med vilje
                .verifyComplete();
    }
}
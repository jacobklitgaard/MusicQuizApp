package org.example.musicquizapp.service;

import org.example.musicquizapp.client.DeezerClient;
import org.example.musicquizapp.client.OpenAiClient;
import org.example.musicquizapp.dto.request.SearchRequestDTO;
import org.example.musicquizapp.dto.response.QuizAnswerDTO;
import org.example.musicquizapp.dto.response.QuizQuestionDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MusicQuizService {

    private final DeezerClient deezerClient;
    private final OpenAiClient openAiClient;

    public MusicQuizService(DeezerClient deezerClient, OpenAiClient openAiClient) {
        this.deezerClient = deezerClient;
        this.openAiClient = openAiClient;
    }

    // QUIZ QUESTION
    public Mono<QuizQuestionDTO> generateQuestion(SearchRequestDTO request) {

        return deezerClient.searchTracks(request)
                .flatMap(response -> {

                    var tracks = response.getData();

                    if (tracks == null || tracks.isEmpty()) {
                        return Mono.error(new RuntimeException("No tracks found"));
                    }

                    var playableTracks = tracks.stream()
                            .filter(t -> t.getPreview() != null && !t.getPreview().isBlank())
                            .toList();

                    if (playableTracks.size() < 3) {
                        return Mono.error(new RuntimeException("Not enough tracks"));
                    }

                    var correctTrack = playableTracks.get(
                            (int) (Math.random() * playableTracks.size())
                    );

                    var wrongOptions = playableTracks.stream()
                            .filter(t -> !t.getId().equals(correctTrack.getId()))
                            .map(t -> t.getName())
                            .filter(name -> name != null)
                            .limit(2)
                            .toList();

                    var options = new ArrayList<String>();
                    options.add(correctTrack.getName());
                    options.addAll(wrongOptions);
                    Collections.shuffle(options);

                    String preview = correctTrack.getPreview();

                    String type = Math.random() > 0.5 ? "TITLE" : "ARTIST";

                    String correctAnswer = "ARTIST".equals(type)
                            ? correctTrack.getArtistName()
                            : correctTrack.getName();

                    return openAiClient.generateOpenAiQuestion(
                                    type,
                                    correctTrack.getName(),
                                    correctTrack.getArtistName(),
                                    ""
                            )
                            .onErrorReturn("Hvilken sang er dette?")
                            .map(aiQuestion ->
                                    new QuizQuestionDTO(
                                            aiQuestion,
                                            options,
                                            correctAnswer,
                                            type,
                                            preview,
                                            correctTrack.getId()
                                    )
                            );
                });
    }

    // BONUS QUESTION (FIXED)
    public Mono<QuizQuestionDTO> generateBonusQuestion(String trackId) {

        return deezerClient.getTrack(trackId)
                .map(track -> {

                    String preview = track.getPreview();

                    String bonusType = switch ((int) (Math.random() * 3)) {
                        case 0 -> "YEAR";
                        case 1 -> "ENERGY";
                        default -> "BPM";
                    };

                    String question = switch (bonusType) {
                        case "YEAR" -> "Hvilket år blev denne sang udgivet?";
                        case "ENERGY" -> "Hvor energisk er denne sang (lav, medium, høj)?";
                        case "BPM" -> "Hvad er tempoet (BPM) i denne sang?";
                        default -> "Bonus spørgsmål";
                    };

                    String correctAnswer = switch (bonusType) {
                        case "YEAR" -> "2010";
                        case "ENERGY" -> "medium";
                        case "BPM" -> "120";
                        default -> "";
                    };

                    return new QuizQuestionDTO(
                            question,
                            List.of("low", "medium", "high"),
                            correctAnswer,
                            bonusType,
                            preview,
                            track.getId()
                    );
                });
    }

    // ANSWER CHECK (FIXED SAFE STRING HANDLING)
    public Mono<Boolean> checkAnswer(QuizAnswerDTO answer) {

        return deezerClient.getTrack(answer.getTrackId())
                .map(track -> {

                    if (track == null) return false;

                    String userAnswer = answer.getAnswer();

                    if (userAnswer == null || userAnswer.isBlank()) {
                        return false;
                    }

                    userAnswer = userAnswer.trim();

                    String trackName = track.getName();

                    String artistName = track.getArtist() != null
                            ? track.getArtist().getName()
                            : "";

                    return userAnswer.equalsIgnoreCase(trackName)
                            || userAnswer.equalsIgnoreCase(artistName);
                });
    }
}
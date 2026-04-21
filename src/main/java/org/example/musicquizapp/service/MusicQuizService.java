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

    public Mono<QuizQuestionDTO> generateQuestion(SearchRequestDTO request) {

        return deezerClient.searchTracks(request)
                .flatMap(response -> {

                    var tracks = response.getData();

                    System.out.println("🎵 Tracks fra Deezer: " + (tracks != null ? tracks.size() : "null"));

                    if (tracks == null || tracks.isEmpty()) {
                        return Mono.just(emptyQuiz("Ingen sange fundet – prøv en anden genre"));
                    }

                    // Forsøg 1: kun tracks med preview
                    var playableTracks = new ArrayList<>(tracks.stream()
                            .filter(t -> t.getPreview() != null && !t.getPreview().isBlank())
                            .toList());

                    System.out.println("🎵 Tracks med preview: " + playableTracks.size());

                    // Forsøg 2: brug alle tracks hvis for få med preview
                    if (playableTracks.size() < 3) {
                        playableTracks = new ArrayList<>(tracks);
                        System.out.println("⚠️ Falder tilbage til alle tracks: " + playableTracks.size());
                    }

                    // Stadig for få — giv op
                    if (playableTracks.size() < 3) {
                        return Mono.just(emptyQuiz("Ikke nok sange – prøv en anden genre"));
                    }

                    Collections.shuffle(playableTracks);

                    var correctTrack = playableTracks.get(0);

                    System.out.println("✅ Valgt sang: " + correctTrack.getName() + " – " + correctTrack.getArtistName());

                    var wrongOptions = playableTracks.stream()
                            .filter(t -> !t.getId().equals(correctTrack.getId()))
                            .map(t -> t.getName())
                            .filter(name -> name != null && !name.isBlank())
                            .distinct()
                            .limit(2)
                            .toList();

                    var options = new ArrayList<String>();
                    options.add(correctTrack.getName());
                    options.addAll(wrongOptions);
                    Collections.shuffle(options);

                    return Mono.just(new QuizQuestionDTO(
                            "Hvad hedder sangen?",
                            options,
                            correctTrack.getName(),
                            "TITLE",
                            correctTrack.getPreview(),
                            correctTrack.getId(),
                            correctTrack.getArtistName()
                    ));
                });
    }

    public Mono<Boolean> checkAnswer(QuizAnswerDTO answer) {

        return deezerClient.getTrack(answer.getTrackId())
                .map(track -> {

                    if (track == null || answer.getAnswer() == null) {
                        return false;
                    }

                    return answer.getAnswer().trim().equalsIgnoreCase(track.getName());
                });
    }

    private QuizQuestionDTO emptyQuiz(String message) {
        return new QuizQuestionDTO(
                message,
                List.of("Ingen data"),
                null,
                "TITLE",
                null,
                null,
                null
        );
    }
}
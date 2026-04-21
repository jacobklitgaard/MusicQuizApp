package org.example.musicquizapp.client;

import org.example.musicquizapp.dto.deezer.TrackDTO;
import org.example.musicquizapp.dto.deezer.TrackSearchResponse;
import org.example.musicquizapp.dto.request.SearchRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DeezerClient {

    private final WebClient webClient;

    private static final Map<String, String> GENRE_IDS = Map.of(
            "pop",     "132",
            "rock",    "152",
            "hiphop",  "116",
            "jazz",    "129"
    );

    public DeezerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<TrackSearchResponse> searchTracks(SearchRequestDTO request) {
        String genreId = GENRE_IDS.getOrDefault(request.getGenre(), "132");

        System.out.println("🔍 Deezer genre ID: " + genreId + " (" + request.getGenre() + ")");

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.deezer.com")
                        .path("/chart/" + genreId + "/tracks")
                        .queryParam("limit", 100)
                        .build())
                .retrieve()
                .bodyToMono(TrackSearchResponse.class);
    }

    public Mono<TrackDTO> getTrack(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.deezer.com")
                        .pathSegment("track", id)
                        .build())
                .retrieve()
                .bodyToMono(TrackDTO.class);
    }
}
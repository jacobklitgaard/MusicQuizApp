package org.example.musicquizapp.client;

import org.example.musicquizapp.dto.deezer.TrackDTO;
import org.example.musicquizapp.dto.deezer.TrackSearchResponse;
import org.example.musicquizapp.dto.request.SearchRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class DeezerClient {

    private final WebClient webClient;

    public DeezerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    // SEARCH TRACKS (FIXED)
    public Mono<TrackSearchResponse> searchTracks(SearchRequestDTO request) {

        String query = buildQuery(request.getGenre(), request.getDecade());

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.deezer.com")
                        .path("/search")
                        .queryParam("q", query)
                        .build())
                .retrieve()
                .bodyToMono(TrackSearchResponse.class);
    }

    // GET SINGLE TRACK (OK som den er)
    public Mono<TrackDTO> getTrack(String id) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api.deezer.com")
                        .path("/track/" + id)
                        .build())
                .retrieve()
                .bodyToMono(TrackDTO.class);
    }

    // QUERY BUILDER (OK)
    private String buildQuery(String genre, String decade) {

        int yearFrom = Integer.parseInt(decade);
        int yearTo = yearFrom + 9;

        return genre + " year:" + yearFrom + "-" + yearTo;
    }
}
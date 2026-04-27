package org.example.musicquizapp.client;

import org.example.musicquizapp.dto.deezer.TrackDTO;
import org.example.musicquizapp.dto.deezer.TrackSearchResponse;
import org.example.musicquizapp.dto.request.SearchRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DeezerClient {

    private final WebClient webClient;

    private static final Map<String, List<String>> GENRE_ARTISTS = Map.of(
            "pop", List.of(
                    "Michael Jackson", "Madonna", "ABBA", "Whitney Houston",
                    "Britney Spears", "Beyonce", "Taylor Swift", "Adele",
                    "Elton John", "Prince", "George Michael", "Mariah Carey",
                    "Katy Perry", "Lady Gaga", "Rihanna", "Justin Timberlake",
                    "Bruno Mars", "Ed Sheeran", "Ariana Grande", "Dua Lipa",
                    "Shakira", "Jennifer Lopez", "Destiny's Child", "Spice Girls"
            ),
            "rock", List.of(
                    "Queen", "The Beatles", "Rolling Stones", "AC/DC",
                    "Nirvana", "U2", "Guns N Roses", "Bruce Springsteen",
                    "Led Zeppelin", "David Bowie", "Metallica", "Foo Fighters",
                    "Pink Floyd", "The Who", "Aerosmith", "Red Hot Chili Peppers",
                    "Pearl Jam", "Radiohead", "The Cure", "Fleetwood Mac",
                    "Bon Jovi", "Deep Purple", "Black Sabbath", "The Killers"
            ),
            "hiphop", List.of(
                    "Eminem", "Jay-Z", "Tupac", "Notorious B.I.G.",
                    "Kanye West", "Drake", "Kendrick Lamar", "Snoop Dogg",
                    "Dr. Dre", "Nas", "Lauryn Hill", "OutKast",
                    "50 Cent", "Lil Wayne", "Nicki Minaj", "Cardi B",
                    "Ice Cube", "Public Enemy", "A Tribe Called Quest", "Wu-Tang Clan",
                    "Missy Elliott", "Ludacris", "T.I.", "Rick Ross"
            ),
            "jazz", List.of(
                    "Miles Davis", "John Coltrane", "Louis Armstrong",
                    "Ella Fitzgerald", "Duke Ellington", "Billie Holiday",
                    "Dave Brubeck", "Thelonious Monk", "Herbie Hancock", "Nina Simone",
                    "Charlie Parker", "Dizzy Gillespie", "Charles Mingus", "Bill Evans",
                    "Chet Baker", "Stan Getz", "Wes Montgomery", "Oscar Peterson",
                    "Sarah Vaughan", "Nat King Cole"
            )
    );

    public DeezerClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<TrackSearchResponse> searchTracks(SearchRequestDTO request) {
        List<String> artists = GENRE_ARTISTS.getOrDefault(request.getGenre(), GENRE_ARTISTS.get("pop"));

        List<String> shuffled = new ArrayList<>(artists);
        Collections.shuffle(shuffled);
        List<String> picked = shuffled.subList(0, Math.min(3, shuffled.size()));

        System.out.println("🎤 Søger på kunstnere: " + picked);

        List<Mono<TrackSearchResponse>> calls = picked.stream()
                .map(artist -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .scheme("https")
                                .host("api.deezer.com")
                                .path("/search")
                                .queryParam("q", "artist:\"" + artist + "\"")
                                .queryParam("limit", 40)
                                .queryParam("order", "RANKING")
                                .build())
                        .retrieve()
                        .bodyToMono(TrackSearchResponse.class)
                        .onErrorReturn(new TrackSearchResponse()))
                .toList();

        return Mono.zip(calls, responses -> {
            List<TrackDTO> combined = new ArrayList<>();
            for (Object r : responses) {
                TrackSearchResponse res = (TrackSearchResponse) r;
                if (res.getData() != null) {
                    combined.addAll(res.getData());
                }
            }
            System.out.println("🎵 Samlet antal tracks: " + combined.size());
            TrackSearchResponse result = new TrackSearchResponse();
            result.setData(combined);
            return result;
        });
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
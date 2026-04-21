package org.example.musicquizapp.dto.deezer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// FIX: Manglede @Getter/@Setter — Jackson kunne ikke deserialisere Deezer-svaret
@Getter
@Setter
public class TrackSearchResponse {
    private List<TrackDTO> data;
}
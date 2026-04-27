package org.example.musicquizapp.dto.deezer;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrackSearchResponse {
    private List<TrackDTO> data;
}
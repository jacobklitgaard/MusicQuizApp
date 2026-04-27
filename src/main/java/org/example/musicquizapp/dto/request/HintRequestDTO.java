package org.example.musicquizapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HintRequestDTO {
    private String trackName;
    private String artistName;
}
package org.example.musicquizapp.dto.deezer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class AudioFeaturesDTO {
    private String energy;
    private String danceability;
    private int tempo;
}
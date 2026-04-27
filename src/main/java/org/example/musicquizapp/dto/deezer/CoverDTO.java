package org.example.musicquizapp.dto.deezer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoverDTO {

    private String title;

    @JsonProperty("cover_medium")
    private String coverMedium;
}
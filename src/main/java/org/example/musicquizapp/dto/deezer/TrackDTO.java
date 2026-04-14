package org.example.musicquizapp.dto.deezer;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackDTO {

    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("preview")
    private String preview;

    private ArtistDTO artist;

    public String getName() {
        return title;
    }

    public String getPreviewUrl() {
        return preview;
    }

    public String getArtistName() {
        return artist != null ? artist.getName() : "Unknown";
    }
}
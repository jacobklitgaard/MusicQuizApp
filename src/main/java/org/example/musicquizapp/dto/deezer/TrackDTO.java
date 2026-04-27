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
    private CoverDTO album;

    public String getName() {
        return title;
    }

    public String getPreviewUrl() {
        return preview;
    }

    public String getArtistName() {
        return artist != null ? artist.getName() : "Unknown";
    }

    public String getAlbumCover() {
        return album != null ? album.getCoverMedium() : null; // 👈
    }
}
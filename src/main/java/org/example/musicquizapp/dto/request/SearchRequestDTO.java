package org.example.musicquizapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDTO {
    private String genre;
    private String decade;

}
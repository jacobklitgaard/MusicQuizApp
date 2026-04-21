package org.example.musicquizapp.dto.request;

import lombok.Getter;
import lombok.Setter;

// FIX: ChatRequestDTO og Message er nu i egne filer — fjernet herfra
@Getter
@Setter
public class SearchRequestDTO {
    private String genre;
    private String decade;
}
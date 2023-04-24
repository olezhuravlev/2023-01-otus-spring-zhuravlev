package ru.otus.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.shared.model.Genre;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GenreDto {

    private String id;
    private String name;

    public static GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}

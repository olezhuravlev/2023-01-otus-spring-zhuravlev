package ru.otus.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.shared.model.Author;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthorDto {

    private String id;
    private String name;

    public static AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getName());
    }
}

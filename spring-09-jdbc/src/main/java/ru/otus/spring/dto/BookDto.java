package ru.otus.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.spring.validation.HasId;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class BookDto {

    private String id;

    @NotBlank(message = "{field-must-be-filled}")
    @Size(min = 2, max = 256, message = "{expected-size-2-256}")
    private String title;

    @HasId(message = "{field-must-be-filled}")
    private Author author;

    @HasId(message = "{field-must-be-filled}")
    private Genre genre;

    private List<String> bookCommentsToDelete;

    public static BookDto toDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(), new ArrayList<>());
    }
}

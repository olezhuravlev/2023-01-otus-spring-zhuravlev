package ru.otus.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.shared.model.Book;
import ru.otus.shared.validation.HasId;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookDto {

    private String id;

    @NotBlank(message = "{field-must-be-filled}")
    @Size(min = 2, max = 256, message = "{expected-size-2-256}")
    private String title;

    @HasId(message = "{field-must-be-filled}")
    private String authorId;
    private String authorName;

    @HasId(message = "{field-must-be-filled}")
    private String genreId;
    private String genreName;

    private List<String> bookCommentsToDelete;

    public static BookDto toDto(Book book) {
        return new BookDto(book.getId(), book.getTitle(),
                book.getAuthorId(), book.getAuthorName(),
                book.getGenreId(), book.getGenreName(),
                new ArrayList<>());
    }
}

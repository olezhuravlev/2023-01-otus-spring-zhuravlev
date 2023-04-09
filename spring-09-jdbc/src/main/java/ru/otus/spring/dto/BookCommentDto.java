package ru.otus.spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.spring.model.BookComment;

@AllArgsConstructor
@Data
public class BookCommentDto {

    private String id;
    private String bookId;

    @NotBlank(message = "{field-must-be-filled}")
    @Size(min = 2, max = 256, message = "{expected-size-2-256}")
    private String text;

    public static BookCommentDto toDto(BookComment bookComment) {
        return new BookCommentDto(bookComment.getId(), bookComment.getText(), bookComment.getBookId());
    }
}

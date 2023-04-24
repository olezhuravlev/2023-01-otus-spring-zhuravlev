package ru.otus.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "book_comments")
public class BookComment {

    @Id
    private String id;
    private String bookId;
    private String text;

    @PersistenceCreator
    public BookComment(String bookId, String text) {
        this.bookId = bookId;
        this.text = text;
    }
}

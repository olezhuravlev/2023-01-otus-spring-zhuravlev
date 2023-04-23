package ru.otus.spring.model;

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
    private String text;
    private String bookId;

    @PersistenceCreator
    public BookComment(String text, String bookId) {
        this.text = text;
        this.bookId = bookId;
    }
}

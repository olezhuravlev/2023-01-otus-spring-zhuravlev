package ru.otus.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "book_comments")
public class BookComment {

    @Id
    private ObjectId id;
    private String text;
    private String bookId;

    @PersistenceCreator
    public BookComment(String text, String bookId) {
        this.text = text;
        this.bookId = bookId;
    }
}

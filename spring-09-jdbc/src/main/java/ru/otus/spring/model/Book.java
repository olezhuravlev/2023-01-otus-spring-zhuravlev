package ru.otus.spring.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {

    @Id
    private String id;

    private String title;

    @DBRef
    private Author author;

    @DBRef
    private Genre genre;

    @DBRef
    private List<BookComment> bookComments;

    @PersistenceCreator
    public Book(String title, Author author, Genre genre, List<BookComment> bookComments) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.bookComments = bookComments;
    }

    public void deleteBookComment(String bookCommentId) {
        bookComments.removeIf(bookComment -> bookComment.getId().equals(bookCommentId));
    }
}

package ru.otus.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {

    @Id
    private String id;
    private String title;
    private String authorId;
    private String authorName;
    private String genreId;
    private String genreName;

    public Book(String id, String title, String authorId, String genreId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreId = genreId;
    }

    @PersistenceCreator
    public Book(String title, String authorId, String genreId) {
        this.title = title;
        this.authorId = authorId;
        this.genreId = genreId;
    }
}

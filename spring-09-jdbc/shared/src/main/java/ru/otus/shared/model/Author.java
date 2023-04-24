package ru.otus.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "authors")
public class Author {

    @Id
    private String id;
    private String name;

    @PersistenceCreator
    public Author(String name) {
        this.name = name;
    }
}

package ru.otus.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@Document(collection = "genres")
public class Genre {

    @Id
    private String id;
    private String name;

    @PersistenceCreator
    public Genre(String name) {
        this.name = name;
    }
}

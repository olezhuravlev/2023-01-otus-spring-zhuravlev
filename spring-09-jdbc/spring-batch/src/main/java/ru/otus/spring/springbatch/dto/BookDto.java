package ru.otus.spring.springbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookDto {
    private long id;
    private String title;
    private long authorId;
    private long genreId;
}

package ru.otus.spring.springbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BookCommentDto {
    private long id;
    private long bookId;
    private String text;
}

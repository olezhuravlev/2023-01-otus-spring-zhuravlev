package ru.otus.spring.springbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthorDto {
    private long id;
    private String name;
}

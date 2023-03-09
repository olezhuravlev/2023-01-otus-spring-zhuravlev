package ru.otus.spring.dao;

import ru.otus.spring.model.Book;

import java.util.List;

public interface BookDao {
    List<Book> read();
    Book read(long id);
    List<Book> read(String title);
    int create(String title, long id_author, long id_genre);
    int update(long id, String title, long id_author, long id_genre);
    int delete(long id);
}

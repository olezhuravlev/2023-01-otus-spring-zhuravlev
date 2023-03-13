package ru.otus.spring.repositories;

import ru.otus.spring.model.BookComment;

import java.util.List;

public class BookCommentRepoJpa implements BookCommentRepo {

    @Override
    public BookComment read(long id) {
        return null;
    }
    @Override
    public List<BookComment> readAll(long id_book) {
        return null;
    }
    @Override
    public int create(long id_book, String text) {
        return 0;
    }
    @Override
    public int update(long id_book, String text) {
        return 0;
    }
    @Override
    public int delete(long id) {
        return 0;
    }
}

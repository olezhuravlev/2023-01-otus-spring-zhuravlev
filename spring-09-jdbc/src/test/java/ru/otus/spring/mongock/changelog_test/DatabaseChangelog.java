package ru.otus.spring.mongock.changelog_test;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.repository.AuthorRepo;
import ru.otus.spring.repository.BookCommentRepo;
import ru.otus.spring.repository.BookRepo;
import ru.otus.spring.repository.GenreRepo;

import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private final List<Author> authors;
    private final List<Genre> genres;
    private final List<Book> books;
    private final List<BookComment> comments;

    public DatabaseChangelog() {

        authors = new ArrayList<>();
        authors.add(new Author("a1", "Test author 1"));
        authors.add(new Author("a2", "Test author 2"));
        authors.add(new Author("a3", "Test author 3"));

        genres = new ArrayList<>();
        genres.add(new Genre("g1", "Test genre 1"));
        genres.add(new Genre("g2", "Test genre 2"));
        genres.add(new Genre("g3", "Test genre 3"));

        comments = new ArrayList<>();
        comments.add(new BookComment("bc1", "Test book comment 1", "b1"));
        comments.add(new BookComment("bc2", "Test book comment 2", "b2"));
        comments.add(new BookComment("bc3", "Test book comment 3", "b3"));

        books = new ArrayList<>();
        books.add(new Book("b1", "Test book 1", authors.get(0), genres.get(0), new ArrayList<>()));
        books.add(new Book("b2", "Test book 2", authors.get(1), genres.get(1), new ArrayList<>()));
        books.add(new Book("b3", "Test book 3", authors.get(2), genres.get(2), new ArrayList<>()));
    }

    @ChangeSet(order = "001", id = "dropDb", author = "olezhuravlev", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "olezhuravlev")
    public void insertAuthors(AuthorRepo repo) {
        repo.saveAll(authors);
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "olezhuravlev")
    public void insertGenres(GenreRepo repo) {
        repo.saveAll(genres);
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "olezhuravlev")
    public void insertBooks(BookRepo repo) {
        repo.saveAll(books);
    }

    @ChangeSet(order = "005", id = "insertBookComments", author = "olezhuravlev")
    public void insertBookComments(BookCommentRepo repo) {
        repo.saveAll(comments);
    }
}

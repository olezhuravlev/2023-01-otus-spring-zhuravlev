package ru.otus.spring.mongock.changelog;

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
        authors.add(new Author("a0", "Leo Tolstoy"));
        authors.add(new Author("a1", "Lewis Carroll"));
        authors.add(new Author("a2", "Fyodor Dostoevsky"));
        authors.add(new Author("a3", "Robert Louis Stevenson"));
        authors.add(new Author("a4", "John Ronald Reuel Tolkien"));

        genres = new ArrayList<>();
        genres.add(new Genre("g0", "Novel"));
        genres.add(new Genre("g1", "Adventures"));
        genres.add(new Genre("g2", "Fantasy"));

        comments = new ArrayList<>();
        comments.add(new BookComment("bc0", "Nice, although slightly hard for reading.", "b0"));
        comments.add(new BookComment("bc1", "Good reading for an intellectual reader.", "b0"));
        comments.add(new BookComment("bc2", "Brilliant book!", "b1"));
        comments.add(new BookComment("bc3", "Haven't read yet, but the cover is attractive.", "b2"));
        comments.add(new BookComment("bc4", "Mediocre pulp fiction.", "b3"));
        comments.add(new BookComment("bc5", "Not bad, not bad.", "b4"));
        comments.add(new BookComment("bc6", "A masterpiece!", "b5"));

        books = new ArrayList<>();
        books.add(new Book("b0", "Crime and Punishment", authors.get(2), genres.get(0), new ArrayList<>()));
        books.add(new Book("b1", "Anna Karenina", authors.get(0), genres.get(0), new ArrayList<>()));
        books.add(new Book("b2", "Alice in Wonderland", authors.get(1), genres.get(1), new ArrayList<>()));
        books.add(new Book("b3", "War and Peace", authors.get(0), genres.get(0), new ArrayList<>()));
        books.add(new Book("b4", "Treasure Island", authors.get(3), genres.get(1), new ArrayList<>()));
        books.add(new Book("b5", "The Lord of the Rings", authors.get(4), genres.get(2), new ArrayList<>()));
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

package ru.otus.spring.service.printers;

import org.springframework.stereotype.Service;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Map;

@Service
public class BookPrinter extends AbstractStringPrinter<Book> {

    @Override
    protected String getKey() {
        return "books";
    }

    @Override
    protected void printRows(List<Book> books, StringBuilder stringBuilder, Map<String, Map<String, String>> columns) {
        for (Book book : books) {
            Author author = book.getAuthor();
            Genre genre = book.getGenre();
            Map<String, String> values = Map.of("id", String.valueOf(book.getId()), "title", book.getTitle(), "author", author.getName(), "genre", genre.getName());
            stringBuilder.append(printValues(values, columns));
        }
    }
}

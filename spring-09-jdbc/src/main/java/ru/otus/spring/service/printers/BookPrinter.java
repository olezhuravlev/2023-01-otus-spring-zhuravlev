package ru.otus.spring.service.printers;

import org.springframework.stereotype.Service;
import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Map;

@Service
public class BookPrinter extends AbstractStringPrinter<Book> {

    @Override
    protected String getKey() {
        return "books";
    }

    @Override
    protected void printRows(List<Book> books, StringBuilder stringBuilder) {
        for (Book book : books) {
            Map<String, String> values = Map.of("id", String.valueOf(book.getId()), "title", book.getTitle(), "author", book.getAuthor(), "genre", book.getGenre());
            Map<String, Map<String, String>> columns = printProps.getColumns(getKey());
            stringBuilder.append(printValues(values, columns));
        }
    }
}

package ru.otus.spring.service.printers;

import org.springframework.stereotype.Service;
import ru.otus.spring.model.Author;

import java.util.List;
import java.util.Map;

@Service
public class AuthorPrinter extends AbstractStringPrinter<Author> {

    @Override
    protected String getKey() {
        return "authors";
    }

    @Override
    protected void printRows(List<Author> authors, StringBuilder stringBuilder, Map<String, Map<String, String>> columns) {
        for (Author author : authors) {
            Map<String, String> values = Map.of("id", String.valueOf(author.getId()), "name", author.getName());
            stringBuilder.append(printValues(values, columns));
        }
    }
}

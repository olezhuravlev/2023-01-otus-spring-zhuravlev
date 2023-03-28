package ru.otus.spring.service.printers;

import org.springframework.stereotype.Service;
import ru.otus.spring.model.Genre;

import java.util.List;
import java.util.Map;

@Service
public class GenrePrinter extends AbstractStringPrinter<Genre> {

    @Override
    protected String getKey() {
        return "genres";
    }

    @Override
    protected void printRows(List<Genre> genres, StringBuilder stringBuilder, Map<String, Map<String, String>> columns) {
        for (Genre genre : genres) {
            Map<String, String> values = Map.of("id", String.valueOf(genre.getId()), "name", genre.getName());
            stringBuilder.append(printValues(values, columns));
        }
    }
}

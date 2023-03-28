package ru.otus.spring.service.printers;

import org.springframework.stereotype.Service;
import ru.otus.spring.model.BookComment;

import java.util.List;
import java.util.Map;

@Service
public class BookCommentPrinter extends AbstractStringPrinter<BookComment> {

    @Override
    protected String getKey() {
        return "bookComments";
    }

    @Override
    protected void printRows(List<BookComment> bookComments, StringBuilder stringBuilder, Map<String, Map<String, String>> columns) {
        for (BookComment bookComment : bookComments) {
            Map<String, String> values = Map.of("id", String.valueOf(bookComment.getId()), "text", bookComment.getText());
            stringBuilder.append(printValues(values, columns));
        }
    }
}

package ru.otus.spring.service.printers;

import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.spring.configs.PrintProps;

import java.util.List;
import java.util.Map;

public abstract class AbstractStringPrinter<T> implements StringPrinter<T> {

    @Autowired
    protected PrintProps printProps;

    protected abstract void printRows(List<T> models, StringBuilder stringBuilder, Map<String, Map<String, String>> columns);
    protected abstract String getKey();

    @Override
    public String print(List<T> models) {

        Map<String, Map<String, String>> columns = printProps.getColumns(getKey());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(printDelimiter(columns))
                .append(printHeaders(columns))
                .append(printDelimiter(columns));
        printRows(models, stringBuilder, columns);
        stringBuilder.append(printDelimiter(columns));
        return stringBuilder.toString();
    }
}

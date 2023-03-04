package ru.otus.spring.service.printers;

import java.util.List;
import java.util.Map;

public interface StringPrinter<T> {

    String HEADER_ID = "header";
    String WIDTH_ID = "width";

    String COLUMN_DELIMITER = "|";
    String ROW_DELIMITER = "-";

    String print(List<T> values);

    default String printValues(Map<String, String> values, Map<String, Map<String, String>> columns) {
        StringBuilder builder = new StringBuilder();
        builder.append(COLUMN_DELIMITER);
        for (Map.Entry<String, Map<String, String>> entry : columns.entrySet()) {
            String columnId = entry.getKey();
            Map<String, String> columnParams = entry.getValue();
            int width = Integer.parseInt(columnParams.get(WIDTH_ID));
            String value = values.get(columnId);
            builder.append(String.format("%-" + width + "." + width + "s" + COLUMN_DELIMITER, value));
        }
        return builder.append(String.format("%n")).toString();
    }

    default String printHeaders(Map<String, Map<String, String>> columns) {
        StringBuilder builder = new StringBuilder();
        builder.append(COLUMN_DELIMITER);
        for (Map.Entry<String, Map<String, String>> entry : columns.entrySet()) {
            Map<String, String> columnParams = entry.getValue();
            int width = Integer.parseInt(columnParams.get(WIDTH_ID));
            String header = columnParams.get(HEADER_ID);
            builder.append(String.format("%-" + width + "." + width + "s" + COLUMN_DELIMITER, header));
        }
        return builder.append(String.format("%n")).toString();
    }

    default String printDelimiter(Map<String, Map<String, String>> columns) {

        StringBuilder builder = new StringBuilder();

        builder.append(COLUMN_DELIMITER);
        for (Map.Entry<String, Map<String, String>> entry : columns.entrySet()) {
            Map<String, String> columnParams = entry.getValue();
            int width = Integer.parseInt(columnParams.get(WIDTH_ID));
            builder.append(ROW_DELIMITER.repeat(width)).append(COLUMN_DELIMITER);
        }
        return builder.append(String.format("%n")).toString();
    }
}

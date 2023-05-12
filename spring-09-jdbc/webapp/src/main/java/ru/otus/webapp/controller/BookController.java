package ru.otus.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.shared.dto.BookCommentDto;
import ru.otus.shared.dto.BookDto;
import ru.otus.webapp.service.ApiGate;

import java.util.List;

@Controller
public class BookController {

    private final ApiGate apiGate;

    public BookController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @GetMapping(value = "/books/{bookId}")
    public String getBookById(@PathVariable("bookId") String bookId, Model model) {

        BookDto dto = apiGate.getBook(bookId);
        model.addAttribute("book", dto);

        List<BookCommentDto> comments = apiGate.getCommentsByBookId(bookId);
        model.addAttribute("comments", comments);

        BookCommentDto blankBookCommentDto = new BookCommentDto("?", "?", "?");
        model.addAttribute("blankBookCommentDto", blankBookCommentDto);

        return "bookForm";
    }
}

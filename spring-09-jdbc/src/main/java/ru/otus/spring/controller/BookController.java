package ru.otus.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Book;
import ru.otus.spring.service.ApiGate;

import java.util.NoSuchElementException;

@Controller
public class BookController {

    private final ApiGate apiGate;

    public BookController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @GetMapping(value = "/books/{bookId}")
    public String getBookById(@PathVariable("bookId") String bookId, Model model) {

        Book book = apiGate.getBook(bookId).orElseThrow(NoSuchElementException::new);
        BookDto dto = BookDto.toDto(book);
        model.addAttribute("book", dto);
        model.addAttribute("comments", book.getBookComments());

        BookCommentDto blankBookCommentDto = new BookCommentDto("?", "?", "?");
        model.addAttribute("blankBookCommentDto", blankBookCommentDto);

        return "bookForm";
    }
}

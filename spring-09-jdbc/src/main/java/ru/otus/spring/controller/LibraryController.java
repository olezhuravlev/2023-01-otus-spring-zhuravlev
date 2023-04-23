package ru.otus.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LibraryController {

    private final ApiGate apiGate;

    public LibraryController(ApiGate apiGate) {
        this.apiGate = apiGate;
    }

    @GetMapping(value = {"/", "books", "books/"})
    public String home(Model model) {

        List<Book> books = apiGate.getBooks();
        List<BookDto> booksDto = books.stream().map(BookDto::toDto).toList();
        model.addAttribute("books", booksDto);

        BookDto blankBookDto = new BookDto("?", "?", new Author("", ""), new Genre("", ""), new ArrayList<>());
        model.addAttribute("blankBookDto", blankBookDto);

        return "home";
    }
}

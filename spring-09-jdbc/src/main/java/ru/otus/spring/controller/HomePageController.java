package ru.otus.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.spring.config.AppProps;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomePageController {

    private final ApiGate apiGate;
    private final AppProps appProps;

    public HomePageController(ApiGate apiGate, AppProps appProps) {
        this.apiGate = apiGate;
        this.appProps = appProps;
    }

    @GetMapping(value = {"/", "books", "books/"})
    public String home(Model model) {

        List<Book> books = apiGate.getBooksWithAuthorAndGenre();
        List<BookDto> booksDto = books.stream().map(BookDto::toDto).toList();
        model.addAttribute("books", booksDto);

        BookDto blankBookDto = new BookDto(appProps.emptyItemId(), "?", new Author(appProps.emptyItemId(), ""), new Genre(appProps.emptyItemId(), ""), new ArrayList<>());
        model.addAttribute("blankBookDto", blankBookDto);

        return "home";
    }
}

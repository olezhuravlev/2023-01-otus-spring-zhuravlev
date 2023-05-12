package ru.otus.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.shared.dto.BookDto;
import ru.otus.webapp.service.ApiGate;

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

        List<BookDto> books = apiGate.getBooks();
        model.addAttribute("books", books);

        BookDto blankBookDto = new BookDto("?", "?", "?", "?", "?", "?", new ArrayList<>());
        model.addAttribute("blankBookDto", blankBookDto);

        return "home";
    }
}

package ru.otus.spring.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.util.StringUtils;
import ru.otus.spring.config.AppProps;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;

import java.util.ArrayList;
import java.util.Collection;
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
    public String home(Model model, @CurrentSecurityContext SecurityContext securityContext) {

        List<Book> books = apiGate.getBooksWithAuthorAndGenre();
        List<BookDto> booksDto = books.stream().map(BookDto::toDto).toList();
        model.addAttribute("books", booksDto);

        BookDto blankBookDto = new BookDto(appProps.emptyItemId(), "?", new Author(appProps.emptyItemId(), ""), new Genre(appProps.emptyItemId(), ""), new ArrayList<>());
        model.addAttribute("blankBookDto", blankBookDto);

        Authentication authentication = securityContext.getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", StringUtils.join(authorities, ","));

        return "home";
    }
}

package ru.otus.spring.controller;

import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.thymeleaf.util.StringUtils;
import ru.otus.spring.config.AppProps;
import ru.otus.spring.dto.BookCommentDto;
import ru.otus.spring.dto.BookDto;
import ru.otus.spring.model.Author;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.model.Genre;
import ru.otus.spring.service.ApiGate;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class BookController {

    private final ApiGate apiGate;
    private final AppProps appProps;

    public BookController(ApiGate apiGate, AppProps appProps) {
        this.apiGate = apiGate;
        this.appProps = appProps;
    }

    @GetMapping(value = "/books/{bookId}")
    public String getBookById(@PathVariable("bookId") long bookId, Model model, @CurrentSecurityContext SecurityContext securityContext) {

        Book book = apiGate.getBookById(bookId).orElseThrow(NoSuchElementException::new);

        book.setAuthor(Hibernate.unproxy(book.getAuthor(), Author.class));
        book.setGenre(Hibernate.unproxy(book.getGenre(), Genre.class));

        // Reverse order or comments.
        List<BookComment> bookComments = book.getBookComments();
        bookComments.sort((o1, o2) -> Long.valueOf(o2.getId()).compareTo(o1.getId()));

        BookDto dto = BookDto.toDto(book);
        model.addAttribute("book", dto);
        model.addAttribute("comments", bookComments);

        BookCommentDto blankBookCommentDto = new BookCommentDto(appProps.emptyItemId(), appProps.emptyItemId(), "?");
        model.addAttribute("blankBookCommentDto", blankBookCommentDto);

        Authentication authentication = securityContext.getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        model.addAttribute("username", authentication.getName());
        model.addAttribute("roles", StringUtils.join(authorities, ","));

        return "bookForm";
    }
}

package ru.otus.spring.repository;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import ru.otus.spring.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepo {

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //@PostFilter("hasPermission(filterObject, 'READ')")

    //@PreFilter("@myAuth.decide(#root)")
    //@PostFilter("@myAuth.decide(#root)") // Invokes

    //@PreAuthorize("hasRole('ROLE_ADMIN')") // Invokes and works!
    //@PreAuthorize("@myAuth.decide(#root)") // Works!
    //@PostAuthorize("hasRole('ROLE_ADMIN')") // Invokes and works!
    @PostAuthorize("@myAuth.decide(#root)") // Works!
    //@PreFilter("@myAuth.decide(#root)")
    List<Book> findAllWithAuthorAndGenre();

    Optional<Book> findById(long id);
    boolean isBookExist(long id);
    Book save(Book book);
    void deleteById(long id);
}

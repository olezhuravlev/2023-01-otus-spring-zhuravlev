package ru.otus.spring.repository.datarest;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.spring.model.Author;

import java.util.List;

@RepositoryRestResource(path = "authors-datarest")
public interface AuthorRepository extends PagingAndSortingRepository<Author, Long> {

    List<Author> findAll();

    @RestResource(path = "id")
    Author findById(Long id);
}

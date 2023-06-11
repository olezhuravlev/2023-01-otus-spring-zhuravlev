package ru.otus.spring.repository.datarest;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.spring.model.Genre;

import java.util.List;

@RepositoryRestResource(path = "genres-datarest")
public interface GenreRepository extends PagingAndSortingRepository<Genre, Long> {

    List<Genre> findAll();

    @RestResource(path = "id")
    Genre findById(Long id);
}

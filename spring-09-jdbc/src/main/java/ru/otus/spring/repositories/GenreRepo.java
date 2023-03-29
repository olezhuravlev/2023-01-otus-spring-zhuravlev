package ru.otus.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.spring.model.Genre;

public interface GenreRepo extends JpaRepository<Genre, Long> {
}

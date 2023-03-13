package ru.otus.spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false, unique = false)
    private String title;

    //@Column(name = "id_author", nullable = false, unique = false)
    @ManyToOne(targetEntity = Author.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    //@Column(name = "id_genre", nullable = false, unique = false)
    @ManyToOne(targetEntity = Genre.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @OneToMany(targetEntity = BookComment.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private List<BookComment> bookComments;
}

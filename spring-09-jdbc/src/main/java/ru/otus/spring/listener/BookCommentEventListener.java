package ru.otus.spring.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import ru.otus.spring.model.Book;
import ru.otus.spring.model.BookComment;
import ru.otus.spring.repository.BookRepo;

import java.util.List;
import java.util.Optional;

public class BookCommentEventListener extends AbstractMongoEventListener<BookComment> {

    @Autowired
    private BookRepo bookRepo;

    @Override
    public void onAfterSave(AfterSaveEvent<BookComment> event) {

        super.onAfterSave(event);

        BookComment bookComment = event.getSource();
        String bookId = bookComment.getBookId();

        Optional<Book> book = bookRepo.findById(bookId);
        Book existingBook = book.get();
        List<BookComment> bookComments = existingBook.getBookComments();
        if (!bookComments.contains(bookComment)) {
            bookComments.add(bookComment);
            bookRepo.save(book.get());
        }
    }
}

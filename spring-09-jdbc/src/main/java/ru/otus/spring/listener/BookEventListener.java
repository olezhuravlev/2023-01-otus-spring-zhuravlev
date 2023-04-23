package ru.otus.spring.listener;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import ru.otus.spring.model.Book;
import ru.otus.spring.repository.BookCommentRepo;

public class BookEventListener extends AbstractMongoEventListener<Book> {

    @Autowired
    private BookCommentRepo bookCommentRepo;

    @Override
    public void onAfterDelete(AfterDeleteEvent<Book> event) {

        super.onAfterDelete(event);

        Document document = event.getSource();
        Object bookId = document.get("_id");

        // Test items has String type of ID!
        if (bookId instanceof String) {
            bookCommentRepo.deleteByBookId((String) bookId);
        } else if (bookId instanceof ObjectId) {
            bookCommentRepo.deleteByBookId(bookId.toString());
        }
    }
}

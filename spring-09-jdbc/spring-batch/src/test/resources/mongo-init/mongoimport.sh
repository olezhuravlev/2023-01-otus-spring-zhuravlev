#!/bin/bash
mongoimport --db librarydb --collection genres --type json --file /initdb/data/genres.json --jsonArray
mongoimport --db librarydb --collection authors --type json --file /initdb/data/authors.json --jsonArray
mongoimport --db librarydb --collection books --type json --file /initdb/data/books.json --jsonArray
mongoimport --db librarydb --collection book_comments --type json --file /initdb/data/book_comments.json --jsonArray

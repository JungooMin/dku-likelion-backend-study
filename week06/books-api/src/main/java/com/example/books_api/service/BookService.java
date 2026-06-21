package com.example.books_api.service;

import com.example.books_api.entity.Book;
import com.example.books_api.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id).orElseThrow();
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book update(Long id, Book book) {
        Book b = findById(id);
        b.setTitle(book.getTitle());
        b.setPrice(book.getPrice());
        b.setAuthor(book.getAuthor());
        return bookRepository.save(b);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }
}
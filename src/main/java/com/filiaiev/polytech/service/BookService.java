package com.filiaiev.polytech.service;

import com.filiaiev.polytech.dto.BookDTO;
import com.filiaiev.polytech.dto.UpdateBookDTO;

import java.util.List;

public interface BookService {

    List<BookDTO> getAllBooks();

    BookDTO getBook(String isbn);

    BookDTO createBook(BookDTO book);

    BookDTO updateOrCreateBook(String isbn, UpdateBookDTO bookDTO);

    BookDTO updateBook(String isbn, UpdateBookDTO bookDTO);

    void deleteBook(String isbn);
}

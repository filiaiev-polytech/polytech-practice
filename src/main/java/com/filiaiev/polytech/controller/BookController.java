package com.filiaiev.polytech.controller;

import com.filiaiev.polytech.dto.BookDTO;
import com.filiaiev.polytech.dto.UpdateBookDTO;
import com.filiaiev.polytech.exception.BookNotFoundException;
import com.filiaiev.polytech.service.BookService;
import com.filiaiev.polytech.validation.groups.UpdateEntireBook;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/books")
@RestController
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{isbn}")
    public BookDTO getBook(@PathVariable String isbn) {
        try {
            return bookService.getBook(isbn);
        }catch (BookNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found!", ex);
        }
    }

    @PostMapping
    public BookDTO createBook(@Validated @RequestBody BookDTO bookUpdateDTO) {
        return bookService.createBook(bookUpdateDTO);
    }

    @PutMapping("/{isbn}")
    public BookDTO updateOrCreateBook(@PathVariable String isbn,
                                      @Validated(UpdateEntireBook.class) @RequestBody UpdateBookDTO bookDTO) {
        return bookService.updateOrCreateBook(isbn, bookDTO);
    }

    @PatchMapping("/{isbn}")
    public BookDTO updatePartialBook(@PathVariable String isbn,
                                  @Validated @RequestBody UpdateBookDTO bookDTO) {
        try {
            return bookService.updateBook(isbn, bookDTO);
        }catch (BookNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with the given isbn does not exists!");
        }
    }

    @DeleteMapping("/{isbn}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable String isbn) {
        try{
            bookService.deleteBook(isbn);
        }catch (BookNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with the given isbn does not exists!", ex);
        }
    }
}

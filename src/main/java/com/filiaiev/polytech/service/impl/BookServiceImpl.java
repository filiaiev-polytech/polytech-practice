package com.filiaiev.polytech.service.impl;

import com.filiaiev.polytech.dto.BookDTO;
import com.filiaiev.polytech.dto.UpdateBookDTO;
import com.filiaiev.polytech.exception.BookNotFoundException;
import com.filiaiev.polytech.mapper.BookMapper;
import com.filiaiev.polytech.model.Book;
import com.filiaiev.polytech.repository.BookRepository;
import com.filiaiev.polytech.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(BookMapper.INSTANCE::bookToBookDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO getBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(BookNotFoundException::new);
        return BookMapper.INSTANCE.bookToBookDTO(book);
    }

    @Override
    public BookDTO createBook(BookDTO book) {
        Book createdBook = bookRepository.saveAndFlush(
                BookMapper.INSTANCE.bookDTOtoBook(book));
        return BookMapper.INSTANCE.bookToBookDTO(createdBook);
    }

    @Override
    public BookDTO updateOrCreateBook(String isbn, UpdateBookDTO bookDTO) {
        Optional<Book> book = bookRepository.findById(isbn);

        if(!book.isPresent()) {
            BookDTO newBook = BookMapper.INSTANCE.updateBookDTOtoBookDTO(bookDTO);
            newBook.setIsbn(isbn);
            return createBook(newBook);
        }

        Book toUpdate = book.get();
        BeanUtils.copyProperties(bookDTO, toUpdate);

        return BookMapper.INSTANCE.bookToBookDTO(toUpdate);
    }

    @Override
    public BookDTO updateBook(String isbn, UpdateBookDTO bookDTO) {
        Book toUpdate = bookRepository.findById(isbn)
                .orElseThrow(BookNotFoundException::new);

        BeanUtils.copyProperties(bookDTO, toUpdate);

        return BookMapper.INSTANCE.bookToBookDTO(toUpdate);
    }

    @Override
    public void deleteBook(String isbn) {
        if(bookRepository.existsById(isbn)) {
            bookRepository.deleteById(isbn);
            return;
        }
        throw new BookNotFoundException();
    }
}

package com.filiaiev.polytech.service;

import com.filiaiev.polytech.dto.BookDTO;
import com.filiaiev.polytech.dto.UpdateBookDTO;
import com.filiaiev.polytech.exception.BookNotFoundException;
import com.filiaiev.polytech.mapper.BookMapper;
import com.filiaiev.polytech.model.Book;
import com.filiaiev.polytech.repository.BookRepository;
import com.filiaiev.polytech.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    private List<Book> books;

    @BeforeEach
    private void prepareData() {
        books = new ArrayList<>();

        books.add(Book.builder()
                .isbn("9780340960196")
                .title("Dune")
                .genreName("Science Fiction")
                .pages(412)
                .basePrice(BigDecimal.valueOf(23.25))
                .quantity(12)
                .publishDate(LocalDate.of(2015, 7, 16))
                .authorName("Frank Herbert").build()
        );
        books.add(Book.builder()
                .isbn("9781408855690")
                .title("Harry Potter and the Order of the Phoenix")
                .genreName("Fantasy")
                .pages(815)
                .basePrice(BigDecimal.valueOf(6.99))
                .quantity(30)
                .publishDate(LocalDate.of(2014, 9, 1))
                .authorName("J. K. Rowling").build()
        );
    }

    @Test
    public void ReturnAllBooks() {
        when(bookRepository.findAll()).thenReturn(books);

        List<BookDTO> allBooks = bookService.getAllBooks();

        assertThat(allBooks, containsInAnyOrder(
                hasProperty("isbn", equalTo("9780340960196")),
                hasProperty("isbn", equalTo("9781408855690"))
                )
        );
    }

    @Test
    public void GetBookWithGivenIsbn_Success() {
        String isbn = "9780340960196";

        when(bookRepository.findById(isbn))
                .thenReturn(books.stream()
                        .filter(v -> v.getIsbn().equals(isbn))
                        .findFirst()
                );

        assertThat(bookService.getBook(isbn).getIsbn(), equalTo(isbn));
    }

    @Test
    public void GetBookWithGivenIsbn_Fail_NotFound() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BookNotFoundException.class,
                () ->  bookService.getBook(anyString()));
    }

    @Test
    public void CreateNewBookAndAddToDataStore() {
        BookDTO newBookDTO = BookDTO.builder()
                .isbn("9780241431108")
                .title("One: Simple One-Pan Wonders")
                .genreName("Cookbook")
                .pages(312)
                .basePrice(BigDecimal.valueOf(28.00))
                .quantity(20)
                .publishDate(LocalDate.of(2022, 9, 1))
                .authorName("Jamie Oliver").build();

        Book newBook = BookMapper.INSTANCE.bookDTOtoBook(newBookDTO);
        int previousBooksSize = books.size();

        when(bookRepository.saveAndFlush(newBook)).then(invocationOnMock -> {
            books.add(newBook);
            return newBook;
        });

        bookService.createBook(newBookDTO);

        assertThat(books, allOf(
                    iterableWithSize(previousBooksSize + 1),
                    hasItem(hasProperty("isbn", equalTo("9780241431108")))
                )
        );
    }

    @Test
    public void UpdateOrCreateEntireBookWithGivenIsbn_Success_Update() {
        String isbn = "9780340960196";

        UpdateBookDTO bookUpdateDTO = UpdateBookDTO.builder()
                .title("Dune")
                .genreName("Fantasy")
                .pages(500)
                .basePrice(BigDecimal.valueOf(50.99))
                .quantity(5)
                .publishDate(LocalDate.of(2022, 2, 12))
                .authorName("Frank Herbert").build();

        when(bookRepository.findById(isbn))
                .thenReturn(books.stream()
                        .filter(v -> v.getIsbn().equals(isbn))
                        .findFirst()
                );

        BookDTO updatedBook = bookService.updateOrCreateBook(isbn, bookUpdateDTO);
        BookDTO previousBook = BookMapper.INSTANCE.updateBookDTOtoBookDTO(bookUpdateDTO);

        assertThat(updatedBook, is(samePropertyValuesAs(previousBook, "isbn")));
    }

    @Test
    public void UpdateOrCreateEntireBookWithGivenIsbn_Success_Create() {
        String isbn = "111111111111";

        UpdateBookDTO bookUpdateDTO = UpdateBookDTO.builder()
                .title("Create")
                .genreName("Created Genre")
                .pages(10)
                .basePrice(BigDecimal.valueOf(10.99))
                .quantity(12)
                .publishDate(LocalDate.of(2022, 7, 16))
                .authorName("Author Author").build();

        when(bookRepository.findById(isbn))
                .thenReturn(Optional.empty());

        when(bookRepository.saveAndFlush(ArgumentMatchers.any(Book.class)))
                .thenAnswer(invocationOnMock -> {
                    books.add(invocationOnMock.getArgument(0));
                    return invocationOnMock.getArgument(0);
                });

        int booksSizeBeforeUpdate = books.size();
        bookService.updateOrCreateBook(isbn, bookUpdateDTO);

        assertThat(books, allOf(
                iterableWithSize(booksSizeBeforeUpdate + 1),
                hasItem(hasProperty("isbn", equalTo(isbn)))
        ));
    }

    @Test
    public void UpdatePartBookWithGivenIsbn_Success() {
        String isbn = "9780340960196";
        String newTitle = "Updated Partially";
        String newAuthorName = "UpdatedAuthor";

        UpdateBookDTO updateBookDTO = UpdateBookDTO.builder()
                .title(newTitle)
                .authorName(newAuthorName).build();

        when(bookRepository.findById(isbn))
                .thenReturn(books.stream()
                        .filter(v -> v.getIsbn().equals(isbn))
                        .findFirst()
                );

        Book oldBook = bookRepository.findById(isbn).get();
        oldBook.setTitle(newTitle);
        oldBook.setAuthorName(newAuthorName);
        Book updatedBook = BookMapper.INSTANCE.bookDTOtoBook(
                bookService.updateBook(isbn, updateBookDTO));

        assertThat(updatedBook, is(samePropertyValuesAs(oldBook)));
    }

    @Test
    public void UpdatePartBookWithGivenIsbn_Fail_NotFound() {
        when(bookRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(BookNotFoundException.class,
                () -> bookService.updateBook(anyString(), new UpdateBookDTO()));
    }

    @Test
    public void DeleteBookWithGivenIsbn_Success() {
        String isbn = "9780340960196";

        when(bookRepository.existsById(isbn))
                .thenReturn(books.stream()
                        .anyMatch(v -> v.getIsbn().equals(isbn))
                );

        doAnswer(invocationOnMock -> {
            books.removeIf(v -> v.getIsbn().equals(isbn));
            return null;
        }).when(bookRepository).deleteById(isbn);

        bookService.deleteBook(isbn);

        assertThat(books, not(hasItem(
                hasProperty("isbn", equalTo(isbn))))
        );
    }

    @Test
    public void DeleteBookWithGivenIsbn_Fail_NotFound() {
        when(bookRepository.existsById(anyString()))
                .thenReturn(false);

        Assertions.assertThrows(BookNotFoundException.class,
                () -> bookService.deleteBook(anyString()));
    }
}

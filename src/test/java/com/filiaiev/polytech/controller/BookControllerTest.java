package com.filiaiev.polytech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filiaiev.polytech.controller.BookController;
import com.filiaiev.polytech.dto.BookDTO;
import com.filiaiev.polytech.dto.UpdateBookDTO;
import com.filiaiev.polytech.exception.BookNotFoundException;
import com.filiaiev.polytech.mapper.BookMapper;
import com.filiaiev.polytech.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<BookDTO> books;

    @BeforeEach
    private void prepareData() {
        books = new ArrayList<>();

        books.add(BookDTO.builder()
                .isbn("9780340960196")
                .title("Dune")
                .genreName("Science Fiction")
                .pages(412)
                .basePrice(BigDecimal.valueOf(23.25))
                .quantity(12)
                .publishDate(LocalDate.of(2015, 7, 16))
                .authorName("Frank Herbert").build()
        );
        books.add(BookDTO.builder()
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
    public void getAllBooks_Should_ReturnBookCollection() throws Exception {
        when(bookService.getAllBooks()).thenReturn(books);

        mvc.perform(
                get("/api/v1/books"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getBookWithGivenIsbn_Should_ReturnBook_When_BookIsFound() throws Exception {
        String isbn = "9780340960196";
        when(bookService.getBook(isbn))
                .thenReturn(books.stream()
                        .filter(v -> v.getIsbn().equals(isbn))
                        .findFirst().get()
                );

        mvc.perform(
                get("/api/v1/books/{isbn}", isbn))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", is("9780340960196")));
    }

    @Test
    public void getBookWithGivenIsbn_Should_ReturnNotFound_When_BookIsNotFound() throws Exception {
        when(bookService.getBook(anyString()))
                .thenThrow(BookNotFoundException.class);

        mvc.perform(
                get("/api/v1/books/{isbn}", "notfound"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void createBook_Should_ReturnCreatedInstance() throws Exception {
        BookDTO createBook = BookDTO.builder()
                .isbn("4343848235")
                .title("New book")
                .genreName("New genre")
                .pages(111)
                .basePrice(BigDecimal.valueOf(11.99))
                .quantity(85)
                .publishDate(LocalDate.of(2022, 7, 16))
                .authorName("New Author").build();

        when(bookService.createBook(createBook))
                .thenReturn(createBook);

        mvc.perform(
                post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBook)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", equalTo(createBook.getIsbn())));
    }

    @Test
    public void updateOrCreateBook_Should_ReturnUpdatedOrCreatedInstance() throws Exception {
        String isbn = "9780340960196";
        UpdateBookDTO book = UpdateBookDTO.builder()
                .title("Updated book")
                .genreName("Updated genre")
                .pages(412)
                .basePrice(BigDecimal.valueOf(11.99))
                .quantity(85)
                .publishDate(LocalDate.of(2022, 7, 16))
                .authorName("Updated Author").build();

        BookDTO bookDTO = BookMapper.INSTANCE.updateBookDTOtoBookDTO(book);
        bookDTO.setIsbn(isbn);

        when(bookService.updateOrCreateBook(isbn, book))
                .thenReturn(bookDTO);

        mvc.perform(
                put("/api/v1/books/{isbn}", isbn)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn", equalTo(isbn)));
    }

    @Test
    public void updatePartBook_Should_ReturnUpdatedInstance_When_BookIsFound() throws Exception {
        String isbn = "9780340960196";
        UpdateBookDTO updateBook = UpdateBookDTO.builder()
                .title("Updated title").build();

        when(bookService.updateBook(isbn, updateBook))
                    .thenReturn(BookMapper.INSTANCE.updateBookDTOtoBookDTO(updateBook)
                );

        mvc.perform(
                patch("/api/v1/books/{isbn}", isbn)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBook)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", equalTo("Updated title")));
    }

    @Test
    public void updatePartBook_Should_ReturnBadRequest_When_BookNotFound() throws Exception {
        doThrow(BookNotFoundException.class)
                .when(bookService).updateBook(anyString(), any(UpdateBookDTO.class));

        mvc.perform(
                patch("/api/v1/books/{isbn}", "notfound")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"title\" : \"fail\"}"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteBook_Should_ReturnNoContentStatus_When_BookIsFound() throws Exception {
        String isbn = "9780340960196";
        doNothing().when(bookService).deleteBook(anyString());

        mvc.perform(
                delete("/api/v1/books/{isbn}", isbn))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteBook_Should_ReturnBadRequest_When_BookIsNotFound() throws Exception {
        doThrow(BookNotFoundException.class)
                .when(bookService).deleteBook(anyString());

        mvc.perform(
                delete("/api/v1/books/{isbn}", "notfound"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}

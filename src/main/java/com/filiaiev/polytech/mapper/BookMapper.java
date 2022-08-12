package com.filiaiev.polytech.mapper;

import com.filiaiev.polytech.dto.BookDTO;
import com.filiaiev.polytech.dto.UpdateBookDTO;
import com.filiaiev.polytech.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Book bookDTOtoBook(BookDTO bookDTO);

    BookDTO bookToBookDTO(Book book);

    BookDTO updateBookDTOtoBookDTO(UpdateBookDTO bookDTO);
}
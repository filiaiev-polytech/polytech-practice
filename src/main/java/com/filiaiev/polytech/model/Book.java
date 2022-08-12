package com.filiaiev.polytech.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String genreName;

    @Column(nullable = false)
    private Integer pages;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column(nullable = false)
    private String authorName;
}

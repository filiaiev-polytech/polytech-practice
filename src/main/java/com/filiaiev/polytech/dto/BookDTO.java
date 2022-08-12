package com.filiaiev.polytech.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDTO {

    @NotNull
    private String isbn;

    @NotNull
    private String title;

    @NotNull
    private String genreName;

    @NotNull
    @Min(value = 0L)
    private Integer pages;

    @NotNull
    @Min(value = 0L)
    private BigDecimal basePrice;

    @NotNull
    @Min(value = 0L)
    private Integer quantity;

    @NotNull
    private LocalDate publishDate;

    @NotNull
    private String authorName;
}

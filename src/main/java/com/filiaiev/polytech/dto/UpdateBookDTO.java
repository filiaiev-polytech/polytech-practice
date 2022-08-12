package com.filiaiev.polytech.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.filiaiev.polytech.validation.groups.UpdateEntireBook;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateBookDTO {

    @NotNull(groups = {UpdateEntireBook.class})
    private String title;

    @NotNull(groups = {UpdateEntireBook.class})
    private String genreName;

    @NotNull(groups = {UpdateEntireBook.class})
    @Min(value = 0L, groups = {Default.class, UpdateEntireBook.class})
    private Integer pages;

    @NotNull(groups = {UpdateEntireBook.class})
    @Min(value = 0L, groups = {Default.class, UpdateEntireBook.class})
    private BigDecimal basePrice;

    @NotNull(groups = {UpdateEntireBook.class})
    @Min(value = 0L, groups = {Default.class, UpdateEntireBook.class})
    private Integer quantity;

    @NotNull(groups = {UpdateEntireBook.class})
    private LocalDate publishDate;

    @NotNull(groups = {UpdateEntireBook.class})
    private String authorName;
}

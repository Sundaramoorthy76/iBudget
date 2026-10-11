package com.moorthy.ibudget.dto;

import com.moorthy.ibudget.model.EntryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetEntryRequest(
        @NotBlank String title,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull EntryType type,
        @NotBlank String category,
        String description,
        @NotNull LocalDate entryDate
) {
}

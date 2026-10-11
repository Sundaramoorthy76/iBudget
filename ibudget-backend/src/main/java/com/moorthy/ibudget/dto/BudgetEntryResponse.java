package com.moorthy.ibudget.dto;

import com.moorthy.ibudget.model.EntryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BudgetEntryResponse(
        Long id,
        String title,
        BigDecimal amount,
        EntryType type,
        String category,
        String description,
        LocalDate entryDate,
        LocalDateTime createdAt
) {
}

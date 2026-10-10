package com.moorthy.ibudget.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_entries")
@Data
@NoArgsConstructor
public class BudgetEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Column(nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category;

    private String description;

    @NotNull(message = "Entry date is required")
    @Column(nullable = false)
    private LocalDate entryDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BudgetEntry(Long id, String title, BigDecimal amount, EntryType type, String category, String description, LocalDate entryDate) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
        this.entryDate = entryDate;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

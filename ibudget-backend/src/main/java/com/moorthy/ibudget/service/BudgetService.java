package com.moorthy.ibudget.service;

import com.moorthy.ibudget.dto.BudgetEntryRequest;
import com.moorthy.ibudget.dto.BudgetEntryResponse;
import com.moorthy.ibudget.dto.BudgetSummaryResponse;
import com.moorthy.ibudget.model.BudgetEntry;
import com.moorthy.ibudget.model.EntryType;
import com.moorthy.ibudget.repository.BudgetEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetEntryRepository budgetEntryRepository;

    public List<BudgetEntryResponse> findAll() {
        return budgetEntryRepository.findAllByOrderByEntryDateDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BudgetEntryResponse> findById(Long id) {
        return budgetEntryRepository.findById(id).map(this::toResponse);
    }

    public BudgetEntryResponse save(BudgetEntryRequest request) {
        return toResponse(budgetEntryRepository.save(toEntity(request)));
    }

    public BudgetEntryResponse update(Long id, BudgetEntryRequest request) {
        BudgetEntry existing = budgetEntryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Budget entry not found"));

        existing.setTitle(request.title());
        existing.setAmount(request.amount());
        existing.setType(request.type());
        existing.setCategory(request.category());
        existing.setDescription(request.description());
        existing.setEntryDate(request.entryDate());

        return toResponse(budgetEntryRepository.save(existing));
    }

    public void deleteById(Long id) {
        budgetEntryRepository.deleteById(id);
    }

    public BudgetSummaryResponse getSummary() {
        BigDecimal totalIncome = budgetEntryRepository.sumByType(EntryType.INCOME);
        BigDecimal totalExpense = budgetEntryRepository.sumByType(EntryType.EXPENSE);

        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        return new BudgetSummaryResponse(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }

    private BudgetEntry toEntity(BudgetEntryRequest request) {
        BudgetEntry entry = new BudgetEntry();
        entry.setTitle(request.title());
        entry.setAmount(request.amount());
        entry.setType(request.type());
        entry.setCategory(request.category());
        entry.setDescription(request.description());
        entry.setEntryDate(request.entryDate());
        return entry;
    }

    private BudgetEntryResponse toResponse(BudgetEntry entry) {
        return new BudgetEntryResponse(
                entry.getId(),
                entry.getTitle(),
                entry.getAmount(),
                entry.getType(),
                entry.getCategory(),
                entry.getDescription(),
                entry.getEntryDate(),
                entry.getCreatedAt()
        );
    }
}

package com.moorthy.ibudget.service;

import com.moorthy.ibudget.model.BudgetEntry;
import com.moorthy.ibudget.model.BudgetSummary;
import com.moorthy.ibudget.model.EntryType;
import com.moorthy.ibudget.repository.BudgetEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetEntryRepository budgetEntryRepository;

    public List<BudgetEntry> findAll() {
        return budgetEntryRepository.findAllByOrderByEntryDateDesc();
    }

    public Optional<BudgetEntry> findById(Long id) {
        return budgetEntryRepository.findById(id);
    }

    public BudgetEntry save(BudgetEntry entry) {
        return budgetEntryRepository.save(entry);
    }

    public BudgetEntry update(Long id, BudgetEntry updatedEntry) {
        BudgetEntry existing = budgetEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Budget entry not found with id: " + id));

        existing.setTitle(updatedEntry.getTitle());
        existing.setAmount(updatedEntry.getAmount());
        existing.setType(updatedEntry.getType());
        existing.setCategory(updatedEntry.getCategory());
        existing.setDescription(updatedEntry.getDescription());
        existing.setEntryDate(updatedEntry.getEntryDate());

        return budgetEntryRepository.save(existing);
    }

    public void deleteById(Long id) {
        budgetEntryRepository.deleteById(id);
    }

    public BudgetSummary getSummary() {
        BigDecimal totalIncome = budgetEntryRepository.sumByType(EntryType.INCOME);
        BigDecimal totalExpense = budgetEntryRepository.sumByType(EntryType.EXPENSE);

        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        return new BudgetSummary(totalIncome, totalExpense, totalIncome.subtract(totalExpense));
    }
}

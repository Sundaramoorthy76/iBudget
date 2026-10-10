package com.moorthy.ibudget;

import com.moorthy.ibudget.model.BudgetEntry;
import com.moorthy.ibudget.model.EntryType;
import com.moorthy.ibudget.service.BudgetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class BudgetServiceTest {

    @Autowired
    private BudgetService budgetService;

    @Test
    void shouldReturnSummaryWithTotals() {
        budgetService.save(new BudgetEntry(null, "Salary", new BigDecimal("2600.00"), EntryType.INCOME, "Salary", "Monthly pay", LocalDate.now()));
        budgetService.save(new BudgetEntry(null, "Groceries", new BigDecimal("145.50"), EntryType.EXPENSE, "Food", "Weekly groceries", LocalDate.now()));

        var summary = budgetService.getSummary();

        assertThat(summary.totalIncome()).isEqualByComparingTo(new BigDecimal("2600.00"));
        assertThat(summary.totalExpense()).isEqualByComparingTo(new BigDecimal("145.50"));
        assertThat(summary.balance()).isEqualByComparingTo(new BigDecimal("2454.50"));
    }

    @Test
    void shouldUpdateExistingEntry() {
        BudgetEntry entry = budgetService.save(new BudgetEntry(null, "Bonus", new BigDecimal("500.00"), EntryType.INCOME, "Salary", "Quarterly bonus", LocalDate.now()));

        BudgetEntry updated = new BudgetEntry(null, "Bonus adjusted", new BigDecimal("650.00"), EntryType.INCOME, "Salary", "Updated bonus", LocalDate.now());
        BudgetEntry result = budgetService.update(entry.getId(), updated);

        assertThat(result.getId()).isEqualTo(entry.getId());
        assertThat(result.getTitle()).isEqualTo("Bonus adjusted");
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("650.00"));
        assertThat(result.getDescription()).isEqualTo("Updated bonus");
    }
}

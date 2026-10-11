package com.moorthy.ibudget;

import com.moorthy.ibudget.dto.BudgetEntryRequest;
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
        budgetService.save(new BudgetEntryRequest(
                "Salary", new BigDecimal("2600.00"), EntryType.INCOME, "Salary", "Monthly pay", LocalDate.now()
        ));
        budgetService.save(new BudgetEntryRequest(
                "Groceries", new BigDecimal("145.50"), EntryType.EXPENSE, "Food", "Weekly groceries", LocalDate.now()
        ));

        var summary = budgetService.getSummary();

        assertThat(summary.totalIncome()).isEqualByComparingTo(new BigDecimal("2600.00"));
        assertThat(summary.totalExpense()).isEqualByComparingTo(new BigDecimal("145.50"));
        assertThat(summary.balance()).isEqualByComparingTo(new BigDecimal("2454.50"));
    }

    @Test
    void shouldUpdateExistingEntry() {
        var entry = budgetService.save(new BudgetEntryRequest(
                "Bonus", new BigDecimal("500.00"), EntryType.INCOME, "Salary", "Quarterly bonus", LocalDate.now()
        ));

        var result = budgetService.update(entry.id(), new BudgetEntryRequest(
                "Bonus adjusted", new BigDecimal("650.00"), EntryType.INCOME, "Salary", "Updated bonus", LocalDate.now()
        ));

        assertThat(result.id()).isEqualTo(entry.id());
        assertThat(result.title()).isEqualTo("Bonus adjusted");
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("650.00"));
        assertThat(result.description()).isEqualTo("Updated bonus");
    }
}

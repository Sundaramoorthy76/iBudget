package com.moorthy.ibudget.model;

import java.math.BigDecimal;

public record BudgetSummary(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance) {
}

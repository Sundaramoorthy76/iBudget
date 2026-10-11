package com.moorthy.ibudget.dto;

import java.math.BigDecimal;

public record BudgetSummaryResponse(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance) {
}

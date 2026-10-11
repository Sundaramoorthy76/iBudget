package com.moorthy.ibudget.dto;

import com.moorthy.ibudget.model.SalaryRecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeSalaryRecordRequest(
        @NotNull SalaryRecordType type,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        @NotNull LocalDate recordDate,
        String description
) {
}

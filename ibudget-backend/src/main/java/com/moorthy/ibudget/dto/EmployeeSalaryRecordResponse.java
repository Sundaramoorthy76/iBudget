package com.moorthy.ibudget.dto;

import com.moorthy.ibudget.model.SalaryRecordType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeSalaryRecordResponse(
        Long id,
        Long employeeId,
        SalaryRecordType type,
        BigDecimal amount,
        LocalDate recordDate,
        String description
) {
}

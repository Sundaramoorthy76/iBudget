package com.moorthy.ibudget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record EmployeeRequest(
        @NotBlank String employeeCode,
        @NotBlank String fullName,
        String department,
        String phone,
        @NotNull @DecimalMin("0.01") BigDecimal monthlySalary,
        Boolean active
) {
}

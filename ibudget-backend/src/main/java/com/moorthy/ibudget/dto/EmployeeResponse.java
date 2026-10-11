package com.moorthy.ibudget.dto;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String employeeCode,
        String fullName,
        String department,
        String phone,
        BigDecimal monthlySalary,
        boolean active
) {
}

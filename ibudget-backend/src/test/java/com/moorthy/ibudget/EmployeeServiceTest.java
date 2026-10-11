package com.moorthy.ibudget;

import com.moorthy.ibudget.dto.EmployeeRequest;
import com.moorthy.ibudget.dto.EmployeeSalaryRecordRequest;
import com.moorthy.ibudget.model.SalaryRecordType;
import com.moorthy.ibudget.service.BudgetService;
import com.moorthy.ibudget.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BudgetService budgetService;

    @Test
    void shouldMaintainEmployeeAdvanceAndDeductionSeparatelyFromBudget() {
        var budgetBefore = budgetService.getSummary();
        var employee = employeeService.createEmployee(new EmployeeRequest(
                " emp-001 ",
                "Alex Employee",
                "Operations",
                "555-0100",
                new BigDecimal("3200.00"),
                true
        ));
        var advance = employeeService.createSalaryRecord(employee.id(), new EmployeeSalaryRecordRequest(
                SalaryRecordType.ADVANCE,
                new BigDecimal("150.00"),
                LocalDate.of(2026, 10, 1),
                "Travel advance"
        ));
        employeeService.createSalaryRecord(employee.id(), new EmployeeSalaryRecordRequest(
                SalaryRecordType.DEDUCTION,
                new BigDecimal("25.00"),
                LocalDate.of(2026, 10, 2),
                "Equipment"
        ));

        assertThat(employee.employeeCode()).isEqualTo("EMP-001");
        assertThat(employee.monthlySalary()).isEqualByComparingTo("3200.00");
        assertThat(employeeService.findSalaryRecords(employee.id()))
                .extracting(record -> record.type())
                .containsExactly(SalaryRecordType.DEDUCTION, SalaryRecordType.ADVANCE);
        assertThat(budgetService.getSummary().totalExpense()).isEqualByComparingTo(budgetBefore.totalExpense());
        assertThat(budgetService.getSummary().totalIncome()).isEqualByComparingTo(budgetBefore.totalIncome());

        employeeService.updateSalaryRecord(employee.id(), advance.id(), new EmployeeSalaryRecordRequest(
                SalaryRecordType.ADVANCE,
                new BigDecimal("125.00"),
                LocalDate.of(2026, 10, 1),
                "Updated travel advance"
        ));
        assertThat(employeeService.findSalaryRecords(employee.id()).get(1).amount())
                .isEqualByComparingTo("125.00");
    }

    @Test
    void shouldRejectDuplicateEmployeeCodesCaseInsensitively() {
        employeeService.createEmployee(new EmployeeRequest(
                "EMP-002", "First Employee", null, null, new BigDecimal("1000.00"), true
        ));

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> employeeService.createEmployee(
                new EmployeeRequest("emp-002", "Second Employee", null, null, new BigDecimal("1200.00"), true)
        )).isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
    }
}

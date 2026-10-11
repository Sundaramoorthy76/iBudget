package com.moorthy.ibudget.controller;

import com.moorthy.ibudget.dto.EmployeeRequest;
import com.moorthy.ibudget.dto.EmployeeResponse;
import com.moorthy.ibudget.dto.EmployeeSalaryRecordRequest;
import com.moorthy.ibudget.dto.EmployeeSalaryRecordResponse;
import com.moorthy.ibudget.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"}, originPatterns = {"https://*.onrender.com", "http://*.onrender.com"})
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeResponse> getEmployees() {
        return employeeService.findAllEmployees();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @PutMapping("/{employeeId}")
    public EmployeeResponse updateEmployee(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeRequest request
    ) {
        return employeeService.updateEmployee(employeeId, request);
    }

    @GetMapping("/{employeeId}/salary-records")
    public List<EmployeeSalaryRecordResponse> getSalaryRecords(@PathVariable Long employeeId) {
        return employeeService.findSalaryRecords(employeeId);
    }

    @PostMapping("/{employeeId}/salary-records")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeSalaryRecordResponse createSalaryRecord(
            @PathVariable Long employeeId,
            @Valid @RequestBody EmployeeSalaryRecordRequest request
    ) {
        return employeeService.createSalaryRecord(employeeId, request);
    }

    @PutMapping("/{employeeId}/salary-records/{recordId}")
    public EmployeeSalaryRecordResponse updateSalaryRecord(
            @PathVariable Long employeeId,
            @PathVariable Long recordId,
            @Valid @RequestBody EmployeeSalaryRecordRequest request
    ) {
        return employeeService.updateSalaryRecord(employeeId, recordId, request);
    }

    @DeleteMapping("/{employeeId}/salary-records/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSalaryRecord(@PathVariable Long employeeId, @PathVariable Long recordId) {
        employeeService.deleteSalaryRecord(employeeId, recordId);
    }
}

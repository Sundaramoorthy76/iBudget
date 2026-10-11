package com.moorthy.ibudget.service;

import com.moorthy.ibudget.dto.EmployeeRequest;
import com.moorthy.ibudget.dto.EmployeeResponse;
import com.moorthy.ibudget.dto.EmployeeSalaryRecordRequest;
import com.moorthy.ibudget.dto.EmployeeSalaryRecordResponse;
import com.moorthy.ibudget.model.Employee;
import com.moorthy.ibudget.model.EmployeeSalaryRecord;
import com.moorthy.ibudget.repository.EmployeeRepository;
import com.moorthy.ibudget.repository.EmployeeSalaryRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeSalaryRecordRepository salaryRecordRepository;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAllEmployees() {
        return employeeRepository.findAllByOrderByFullNameAsc().stream()
                .map(this::toEmployeeResponse)
                .toList();
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        String employeeCode = normalizeEmployeeCode(request.employeeCode());
        if (employeeRepository.existsByEmployeeCodeIgnoreCase(employeeCode)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee code is already in use");
        }

        Employee employee = new Employee();
        applyEmployeeRequest(employee, request, employeeCode);
        return toEmployeeResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
        String employeeCode = normalizeEmployeeCode(request.employeeCode());
        if (employeeRepository.existsByEmployeeCodeIgnoreCaseAndIdNot(employeeCode, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Employee code is already in use");
        }

        applyEmployeeRequest(employee, request, employeeCode);
        return toEmployeeResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public List<EmployeeSalaryRecordResponse> findSalaryRecords(Long employeeId) {
        requireEmployee(employeeId);
        return salaryRecordRepository.findAllByEmployeeIdOrderByRecordDateDescIdDesc(employeeId).stream()
                .map(this::toSalaryRecordResponse)
                .toList();
    }

    public EmployeeSalaryRecordResponse createSalaryRecord(Long employeeId, EmployeeSalaryRecordRequest request) {
        EmployeeSalaryRecord record = new EmployeeSalaryRecord();
        record.setEmployee(requireEmployee(employeeId));
        applySalaryRecordRequest(record, request);
        return toSalaryRecordResponse(salaryRecordRepository.save(record));
    }

    public EmployeeSalaryRecordResponse updateSalaryRecord(
            Long employeeId,
            Long recordId,
            EmployeeSalaryRecordRequest request
    ) {
        EmployeeSalaryRecord record = salaryRecordRepository.findByIdAndEmployeeId(recordId, employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salary record not found"));
        applySalaryRecordRequest(record, request);
        return toSalaryRecordResponse(salaryRecordRepository.save(record));
    }

    public void deleteSalaryRecord(Long employeeId, Long recordId) {
        EmployeeSalaryRecord record = salaryRecordRepository.findByIdAndEmployeeId(recordId, employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salary record not found"));
        salaryRecordRepository.delete(record);
    }

    private Employee requireEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private void applyEmployeeRequest(Employee employee, EmployeeRequest request, String employeeCode) {
        employee.setEmployeeCode(employeeCode);
        employee.setFullName(request.fullName().trim());
        employee.setDepartment(cleanOptionalText(request.department()));
        employee.setPhone(cleanOptionalText(request.phone()));
        employee.setMonthlySalary(request.monthlySalary());
        employee.setActive(request.active() == null || request.active());
    }

    private void applySalaryRecordRequest(EmployeeSalaryRecord record, EmployeeSalaryRecordRequest request) {
        record.setType(request.type());
        record.setAmount(request.amount());
        record.setRecordDate(request.recordDate());
        record.setDescription(cleanOptionalText(request.description()));
    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getDepartment(),
                employee.getPhone(),
                employee.getMonthlySalary(),
                employee.isActive()
        );
    }

    private EmployeeSalaryRecordResponse toSalaryRecordResponse(EmployeeSalaryRecord record) {
        return new EmployeeSalaryRecordResponse(
                record.getId(),
                record.getEmployee().getId(),
                record.getType(),
                record.getAmount(),
                record.getRecordDate(),
                record.getDescription()
        );
    }

    private String normalizeEmployeeCode(String employeeCode) {
        return employeeCode.trim().toUpperCase();
    }

    private String cleanOptionalText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

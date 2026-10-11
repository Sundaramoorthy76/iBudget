package com.moorthy.ibudget.repository;

import com.moorthy.ibudget.model.EmployeeSalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeSalaryRecordRepository extends JpaRepository<EmployeeSalaryRecord, Long> {

    List<EmployeeSalaryRecord> findAllByEmployeeIdOrderByRecordDateDescIdDesc(Long employeeId);

    Optional<EmployeeSalaryRecord> findByIdAndEmployeeId(Long id, Long employeeId);
}

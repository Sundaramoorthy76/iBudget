package com.moorthy.ibudget.repository;

import com.moorthy.ibudget.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByOrderByFullNameAsc();

    boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

    boolean existsByEmployeeCodeIgnoreCaseAndIdNot(String employeeCode, Long id);
}

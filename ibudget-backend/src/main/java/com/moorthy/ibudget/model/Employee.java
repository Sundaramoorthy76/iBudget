package com.moorthy.ibudget.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String employeeCode;

    @Column(nullable = false)
    private String fullName;

    private String department;

    private String phone;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlySalary;

    @Column(nullable = false)
    private boolean active = true;
}

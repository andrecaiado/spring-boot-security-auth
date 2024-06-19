package com.example.springbootsecurityauth.repository;

import com.example.springbootsecurityauth.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository is an interface that provides access to data in a database
 */
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}

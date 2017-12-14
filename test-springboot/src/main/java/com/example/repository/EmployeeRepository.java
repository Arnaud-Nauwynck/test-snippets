package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

	Employee findOneByEmail(String email);

}

package fr.an.testspringbootfastcl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.an.testspringbootfastcl.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {

	Employee findOneByEmail(String email);

}

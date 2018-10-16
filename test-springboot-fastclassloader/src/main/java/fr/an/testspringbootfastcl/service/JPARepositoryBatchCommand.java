package fr.an.testspringbootfastcl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.an.testspringbootfastcl.domain.Employee;
import fr.an.testspringbootfastcl.repository.EmployeeRepository;

@Component
@Transactional
public class JPARepositoryBatchCommand implements CommandLineRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(JPARepositoryBatchCommand.class);
	
	@Autowired
	protected EmployeeRepository employeeDAO;
	
	@Override
	public void run(String... args) throws Exception {
		Employee empById1 = employeeDAO.findById(1).orElse(null);
		if (empById1 != null) {
			LOG.info("Hello employee #1 : " + empById1.getFirstName() + " " + empById1.getLastName());
		}
		Employee empJohn = employeeDAO.findOneByEmail("john.smith@gmail.com");
		if (empJohn == null) {
			empJohn = new Employee();
			empJohn.setEmail("john.smith@gmail.com");
			employeeDAO.save(empJohn);
		}
	}

}

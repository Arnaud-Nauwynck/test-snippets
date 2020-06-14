package fr.an.testhibernatejpafk.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.an.testhibernatejpafk.dto.EmployeeCriteriaDTO;
import fr.an.testhibernatejpafk.dto.EmployeeDTO;
import fr.an.testhibernatejpafk.service.ApiService;

@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private ApiTestService delegate;
	
	@Autowired
	private ApiService apiService;
	
	@GetMapping("/test")
	public void test() throws Exception {
		delegate.testFindAllDTO2();
	}
	
	@GetMapping("/testDto3")
	public void testDto3() throws Exception {
		apiService.listEmployeeDTO3s();
	}
	
	@GetMapping("/testDto3UsingJoin")
	public void testDto3UsingJoin() throws Exception {
		apiService.listEmployeeDTO3Bis_usingJoins();
	}
	
	@GetMapping("/testDto4")
	public void testDto4() throws Exception {
		apiService.listEmployeeDTO4s();
	}
	
	@PutMapping("/findEmployeeByCrit")
	public List<EmployeeDTO> findEmployeeByCrit(@RequestBody EmployeeCriteriaDTO crit) {
		return apiService.findEmployeeByCrit(crit);
	}
	
	
}

package fr.an.tests;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class MyRestController {

	/**
	 * to test...
	 * curl -X GET -H 'Accept: application/json' http://localhost:8080/app/helloParams
	 */
	@GetMapping("/helloParams")
	public Map<String,String> helloParams() {
		Map<String,String> res = new HashMap<>();
		res.put("hello", "world");
		return res;
	}
	
	@GetMapping("/health")
	public Map<String,String> health() {
		Map<String,String> res = new HashMap<>();
		res.put("status", "OK");
		return res;
	}
}

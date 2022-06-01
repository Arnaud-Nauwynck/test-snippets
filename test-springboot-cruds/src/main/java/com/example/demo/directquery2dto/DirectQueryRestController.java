package com.example.demo.directquery2dto;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/directquery")
public class DirectQueryRestController {

	@Autowired
	private DirectQuery2DTOService service;

	@GetMapping("/query1")
	public List<CustomQueryResultDTO> query1() {
		List<CustomQueryResultDTO> res = service.query1("field%");
		return res;
	}
	
}

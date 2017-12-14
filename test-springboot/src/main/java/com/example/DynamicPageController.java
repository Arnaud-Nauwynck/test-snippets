package com.example;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DynamicPageController {

	@RequestMapping("/dynamic-page")
	public String helloWorld(Model model) {
		model.addAttribute("modelMsg", "Hello World");
		return "page-view";
	}

}

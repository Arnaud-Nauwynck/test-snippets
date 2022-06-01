package fr.an.pricing.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1/trade")
public class TradeRestController {

	
	public List<TradeDTO> getTrades() {
		
	}
}

package fr.an.tests.httpproxy.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;


/**
 * 
 */
@RestController
@RequestMapping(path="/api-proxy/foo")
@Slf4j
public class FooProxyRestController {
	
	private String fooTargetUrl = "http://localhost:8080/api/v1/foo";
	
	@RequestMapping("/**")
	public ResponseEntity<?> mirrorRest(
			@RequestBody(required = false) String body, 
			HttpMethod method, 
			HttpServletRequest request, 
			HttpServletResponse response
	    ) throws URISyntaxException {
	    String requestUrl = request.getRequestURI();
	    String targetUrl = requestUrl.substring("/api-proxy/foo".length());
	    		
	    URI uri = UriComponentsBuilder.fromHttpUrl(fooTargetUrl)
	                              .path(targetUrl)
	                              .query(request.getQueryString())
	                              .build(true).toUri();
	    log.info("proxy to http " + method + " '" + uri + "' ...");
	    
	    HttpHeaders headers = new HttpHeaders();
	    Enumeration<String> headerNames = request.getHeaderNames();
	    while (headerNames.hasMoreElements()) {
	        String headerName = headerNames.nextElement();
	        headers.set(headerName, request.getHeader(headerName));
	    }

	    // TODO rewrite header "Host"
	    
	    HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
	    RestTemplate restTemplate = new RestTemplate();
	    try {
	        ResponseEntity<String> res = restTemplate.exchange(uri, method, httpEntity, String.class);
		    log.info(".. done proxy ");
			return res;
	    } catch(HttpStatusCodeException e) {
		    log.warn(".. Failed proxy ");
	        return ResponseEntity.status(e.getRawStatusCode())
	                             .headers(e.getResponseHeaders())
	                             .body(e.getResponseBodyAsString());
	    }
	}

}

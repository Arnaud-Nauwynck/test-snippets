package fr.an.tests.springboothttpfilter.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class BadlyConsumingRequestInputFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain
			) throws ServletException, IOException {
		
		ServletInputStream bodyInputStream = request.getInputStream();
		
		StringBuilder consumed10Chars = new StringBuilder();
		for(int i = 0; i < 10; i++) {
			int b = bodyInputStream.read();
			if (b == -1) {
				break;
			}
			char ch = (char) b;
			consumed10Chars.append(ch);
		}
		
		System.out.println("BadlyConsumingRequestInputFilter => consume 10 chars for body: '" + consumed10Chars + "' .. then filterChain.doFilter(req,resp)");
		filterChain.doFilter(request, response);
	}
	
}

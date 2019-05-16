package fr.an.tests.pringboothttplog.logaspects;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class AppRequestStreamWrapperFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain
			) throws ServletException, IOException {
		
		AppReplayContentHttpServletRequestWrapper requestWrapper = new AppReplayContentHttpServletRequestWrapper(request);
		
		filterChain.doFilter(requestWrapper, response);
	}
	
}

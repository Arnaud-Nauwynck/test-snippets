package fr.an.tests.webfluxservlerlog.ws;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fr.an.tests.webfluxservlerlog.svc.AppRequestTrace;

@Component
public class AppLocalTraceIdHandlerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestUrl = request.getRequestURI();
		String traceId = request.getHeader("trace-id");
		String username = currAuthInfo();
		if (username.equals("admin")) {
			String onBehalfOf = request.getHeader("on-behalf-of");
			if (onBehalfOf != null) {
				username = onBehalfOf;
			}
		}
		
		AppRequestTrace.startRequest(requestUrl, username, traceId);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		AppRequestTrace.endRequest();
	}
	

	private static String currAuthInfo() {
		String res;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			if (auth.isAuthenticated()) {
				String name = auth.getName();
				if ("anonymousUser".equals(name)) {
					// strange in spring
					res = "<anonymous> ";
				} else {
					res = "username:" + auth.getName() + " ";
				}
			} else {
				res = "<unauthenticated> ";
			}
		} else {
			res = "<noauth> ";
		}
		return res;
	}

}

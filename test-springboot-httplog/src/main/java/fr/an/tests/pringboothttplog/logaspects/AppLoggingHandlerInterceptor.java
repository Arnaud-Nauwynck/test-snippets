package fr.an.tests.pringboothttplog.logaspects;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fr.an.tests.pringboothttplog.annotation.NoLog;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppLoggingHandlerInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (isNoLogHandler(handler)) {
			return true;
		}
		String currAuthInfo = currAuthInfo();

		// to consume request body, we need to change the ServletRequest to a ServletRequestWrapper,
		// that (lazily) allow request.getInputStream() to be a replay Buffered InputStream Wrapper
		// for this.. we need to add a servlet.Filter in the filterChain, like AbstractRequestLoggingFilter 
		String reqContentInfo = "";
		if (request instanceof AppReplayContentHttpServletRequestWrapper) {
			AppReplayContentHttpServletRequestWrapper reqWrapper = (AppReplayContentHttpServletRequestWrapper) request;

			// *** consume fully the servletRequest.getInputStream(), and replaced it by a replay from buffer
			byte[] reqContent = reqWrapper.consumeAllForReplay();
			
			if (reqContent == null || reqContent.length == 0) {
				reqContentInfo = "";
			} else {
				int truncLen = Math.min(reqContent.length, 500);
				String charEncoding = request.getCharacterEncoding();
				Charset charset;
				try {
					charset = Charset.forName(charEncoding);
				} catch(Exception ex) {
					// error.. default to UTF-8!
					charset = Charset.forName("UTF-8"); 
				}

				String reqContentText;
				try {
					reqContentText = new String(reqContent, 0, truncLen, charset);
				} catch(RuntimeException ex) {
					reqContentText = "<unknown content, not text>";
					// not a valid string .. charset
				}
				// maybe truncate..
				// remove newLines (by escaping) for logging
				reqContentText = reqContentText.replace("\n", "\\n");

				reqContentInfo = " content (length:" + reqContent.length 
						+ ((truncLen != reqContent.length)? " display truncated to " + truncLen : "")
						+ ") " 
						+ reqContentText;
			}
		}
		
		log.info("preHandle " + currAuthInfo + request.getMethod() + " " + request.getRequestURI()
			// + " handler:" + ((handler != null)? handler : "")
			+ reqContentInfo
			);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		if (isNoLogHandler(handler)) {
			return;
		}
		String currAuthInfo = currAuthInfo();
		if (ex == null) {
			int httpResp = response.getStatus();
			int httpRespFamily = 100 * (httpResp / 100);
			switch (httpRespFamily) {
			case 200:
				log.info("afterCompletion " + currAuthInfo + request.getMethod() + " " + request.getRequestURI()
						+ " => " + httpResp);
				break;
			case 300:
				log.info("afterCompletion " + currAuthInfo + request.getMethod() + " " + request.getRequestURI()
						+ " => " + httpResp);
				break;
			case 400:
				log.warn("afterCompletion " + currAuthInfo + request.getMethod() + " " + request.getRequestURI()
						+ " => " + httpResp);
				break;
			case 500:
				log.error("afterCompletion " + currAuthInfo + request.getMethod() + " " + request.getRequestURI()
						+ " => " + httpResp);
				break;
			default:
				log.error("afterCompletion " + currAuthInfo + request.getMethod() + " " + request.getRequestURI()
						+ " => " + httpResp);
				break;
			}
		} else {
			log.error("afterCompletion " + currAuthInfo + request.getMethod() + " " + request.getRequestURI() + " => "
					+ response.getStatus() + " ex=" + ex.getMessage());
		}
	}

	private String currAuthInfo() {
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

	private boolean isNoLogHandler(Object handler) {
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;
			NoLog noLogAnn = hm.getMethodAnnotation(NoLog.class);
			if (noLogAnn != null) {
				return true;
			}
			// also lookup annotation on class..
			Class<?> handlerMethodClass = hm.getMethod().getDeclaringClass();
			NoLog noLogClassAnn = AnnotatedElementUtils.findMergedAnnotation(handlerMethodClass, NoLog.class);
			if (noLogClassAnn != null) {
				return true;
			}
		}
		return false;
	}

}
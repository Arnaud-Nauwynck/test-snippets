# Test for SpringBoot http request logging aspects


# Sample Curls request, with server-side logs

## Basic Http GET 

curl:

```
curl -u user:password -H "Accept: application/json" http://localhost:8080/api/v1/foo/getFoo

{"strValue":"hello","intValue":123}
```

logs:

```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/foo/getFoo
INFO  f.a.t.p.web.FooRestController - getFoo()
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/foo/getFoo => 200
```


## Basic Http POST/PUT/.. with BodyContent, (consumed+) displayed at the beginning on the log!!

```
curl -v -X POST -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/foo/postFoo -d '{" strValue":"hello","intValue":123}'
```

logs:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user POST /api/v1/foo/postFoo content (length:35) {"strValue":"hello","intValue":123}
INFO  f.a.t.p.web.FooRestController - postFoo()
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user POST /api/v1/foo/postFoo => 200
```



## Advanced Scenarios with erros (anonymous / not permitted / not found / ...)

### Simple call anonymous (with permitAll security)
curl:

```
curl -X GET -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/permitAll/foo/getFoo

{"strValue":"hello","intValue":123}
```

log:

```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle <anonymous> GET /api/v1/permitall/foo/getFoo
INFO  f.a.t.p.w.PermitAllFooRestController2 - getFoo()
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion <anonymous> GET /api/v1/permitall/foo/getFoo => 200
```

### Simple call authenticated with permitAll() security

```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/permitall/foo/getFoo
{"strValue":"hello","intValue":123}
```

log:

```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/permitall/foo/getFoo
INFO  f.a.t.p.w.PermitAllFooRestController2 - getFoo()
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/permitall/foo/getFoo => 200
```


### Failing authenticated call with explicit anonymous() security

cf explicitly added annotation "@NoLog", either on method, or on class

curl:
```
```

log:
```
NO dispatch log!
```


### Calls explicitely mark as not logged

curl:
```
 curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/foo/getFooNoLog

curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/nologfoo/getFoo
```

log:
```
no interceptor log.. only applicative log
```


### Simple call with client-side 404 error
curl:
```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/foo/does-not-exist

{"timestamp":"2019-05-16T04:56:15.327+0000","status":404,"error":"Not Found","message":"No message available","path":"/api/v1/foo/does-not-exist"}
```

log:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/foo/does-not-exist
WARN  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/foo/does-not-exist => 404
```

### Simple call with runtime exception
curl:
```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/foo/getFooRuntimeException

{"timestamp":"2019-05-16T05:41:11.546+0000","status":500,"error":"Internal Server Error","message":"Failed getFooException()","path":"/api/v1/foo/getFooRuntimeException"}
```

log:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/foo/getFooRuntimeException
INFO  f.a.t.p.web.FooRestController - getFooRuntimeException()
ERROR f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/foo/getFooRuntimeException => 200 ex=Failed getFooException()
ERROR o.a.c.c.C.[.[.[.[dispatcherServlet] - Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.RuntimeException: Failed getFooException()] with root cause
java.lang.RuntimeException: nested-exception
    at fr.an.tests.pringboothttplog.web.FooRestController.getFooException(FooRestController.java:46)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:189)
    at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
    at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102)
    at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:892)
    at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797)
    at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
    at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1038)
    at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
    at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1005)
    at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:897)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)
    at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:882)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:320)
    at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:127)
    at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:91)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:119)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:170)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.authentication.www.BasicAuthenticationFilter.doFilterInternal(BasicAuthenticationFilter.java:215)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:116)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.csrf.CsrfFilter.doFilterInternal(CsrfFilter.java:100)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:74)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)
    at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)
    at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:357)
    at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:270)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:92)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:93)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:200)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:200)
    at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
    at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:490)
    at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
    at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
    at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
    at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
    at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)
    at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
    at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:834)
    at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1415)
    at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
    at java.lang.Thread.run(Thread.java:748)
```

### Simple call with checked exception
curl:
```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/foo/getFooCheckedException

{"timestamp":"2019-05-16T05:42:37.805+0000","status":500,"error":"Internal Server Error","message":"Failed getFooCheckedException()","path":"/api/v1/foo/getFooCheckedException"}
```

log:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/foo/getFooCheckedException
INFO  f.a.t.p.web.FooRestController - getFooCheckedException()
ERROR f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/foo/getFooCheckedException => 200 ex=Failed getFooCheckedException()
ERROR o.a.c.c.C.[.[.[.[dispatcherServlet] - Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.Exception: Failed getFooCheckedException()] with root cause
java.lang.RuntimeException: nested-exception
    at fr.an.tests.pringboothttplog.web.FooRestController.getFooCheckedException(FooRestController.java:52)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:498)
    at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:189)
    at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:138)
    at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:102)
    at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:892)
    at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:797)
    at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)
    at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1038)
    at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:942)
    at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1005)
    at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:897)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:634)
    at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:882)
    at javax.servlet.http.HttpServlet.service(HttpServlet.java:741)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:53)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:320)
    at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.invoke(FilterSecurityInterceptor.java:127)
    at org.springframework.security.web.access.intercept.FilterSecurityInterceptor.doFilter(FilterSecurityInterceptor.java:91)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:119)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:137)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:111)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:170)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.authentication.www.BasicAuthenticationFilter.doFilterInternal(BasicAuthenticationFilter.java:215)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.authentication.logout.LogoutFilter.doFilter(LogoutFilter.java:116)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.csrf.CsrfFilter.doFilterInternal(CsrfFilter.java:100)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:74)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:105)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:56)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:334)
    at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:215)
    at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:178)
    at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:357)
    at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:270)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:99)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:92)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.HiddenHttpMethodFilter.doFilterInternal(HiddenHttpMethodFilter.java:93)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:200)
    at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:107)
    at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
    at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
    at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:200)
    at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:96)
    at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:490)
    at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:139)
    at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
    at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:74)
    at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:343)
    at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:408)
    at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:66)
    at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:834)
    at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1415)
    at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
    at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
    at java.lang.Thread.run(Thread.java:748)
```


### unauthorized call on method annoted with "@RolesAllowed("ROLE_ADMIN")"

curl:
```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/securedfoo/getFooAdmin

{"timestamp":"2019-05-16T05:50:08.144+0000","status":404,"error":"Not Found","message":"No message available","path":"/api/v1/securedfoo/getFooAdmin"}
```

log:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/securedfoo/getFooAdmin
WARN  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/securedfoo/getFooAdmin => 404
```

### unauthorized call on controller with .antMatchers(..).hasRole(..)

curl:
```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/admin/getFooAdmin

{"timestamp":"2019-05-16T05:52:55.833+0000","status":403,"error":"Forbidden","message":"Forbidden","path":"/api/v1/admin/getFooAdmin"}
```

log:
```
No interceptor log!!
```



### unauthorized call on controller with .antMatchers(..).hasRole(..)

curl:
```
```

log:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/securedfoo/getFooPreAuthRoleUser
INFO  f.a.t.p.web.SecuredFooRestController - getFooPreAuthRoleUser()
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/securedfoo/getFooPreAuthRoleUser => 200
```



### unauthorized user call on @PreAuthorize("rolesAllowed('ROLE_ADMIN')") ... AND 

curl:
```
curl -X GET -u user:password -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/securedfoo/getFooPreAuthRoleAdmin

{"timestamp":"2019-05-16T06:08:27.751+0000","status":403,"error":"Forbidden","message":"Forbidden","path":"/api/v1/securedfoo/getFooPreAuthRoleAdmin"}
```

log:
```
INFO  f.a.t.p.l.AppLoggingHandlerInterceptor - preHandle username:user GET /api/v1/securedfoo/getFooPreAuthRoleAdmin
ERROR f.a.t.p.l.AppLoggingHandlerInterceptor - afterCompletion username:user GET /api/v1/securedfoo/getFooPreAuthRoleAdmin => 200 ex=Accès refusé
```


### unauthorized anonymous call on @PreAuthorize("isAuthenticated()")

curl:
```
curl -X GET -H "Accept: application/json" -H "Content-Type: application/json" http://localhost:8080/api/v1/securedfoo/getFooAuthenticated

{"timestamp":"2019-05-16T06:00:32.857+0000","status":401,"error":"Unauthorized","message":"Unauthorized","path":"/api/v1/securedfoo/getFooAuthenticated"}
```

log:
```
No interceptor log!
```

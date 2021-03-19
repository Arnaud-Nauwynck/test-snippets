# Test for SpringBoot http filter


This is a sample application showing a javax.servlet.Filter that badly consume the HttpRequestServlet.getInputStream(), so causing corrupted requests in POST and PUT (but ok in GET)


As a reminder, the interface javax.servlet.Filter can be used for 
- inspect http HEADERS for authentication purpose, cookies or user settings preferences
- transforming safely the inputStream/outpustream (for example for adding compression)
- consuming in memory THEN logging + replaying the full steam (for debugging purpose only, not efficient for big requests)

	public interface Filter {
	
	    public default void init(FilterConfig filterConfig) throws ServletException {}
	
	    public void doFilter(
	    			ServletRequest request, 
	    			ServletResponse response,
	            	FilterChain chain
	            	) throws IOException, ServletException;
	
	    public default void destroy() {}
	}


$ curl http://localhost:8080/api/v1/foo/getFoo
=> OK
http 200
{"strValue":"hello","intValue":123}



$ curl -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/v1/foo/postFoo -d '0123456789{"strValue":"hello","intValue":123}'
=>
[INFO] .. BadlyConsumingRequestInputFilter => consume 10 chars for body: '0123456789' .. then filterChain.doFilter(req,resp)

{"strValue":"hello","intValue":123}


curl -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/v1/foo/postFoo -d '{}'
=>
[INFO] .. BadlyConsumingRequestInputFilter => consume 10 chars for body: '{}' .. then filterChain.doFilter(req,resp)

{"timestamp":"2021-03-19T07:32:23.206+0000","status":400,"error":"Bad Request","message":"Required request body is missing: public fr.an.tests.springboothttpfilter.dto.FooResponse fr.an.tests.springboothttpfilter.rest.FooRestController.postFoo(fr.an.tests.springboothttpfilter.dto.FooRequest)","path":"/api/v1/foo/postFoo"}



curl -H "Content-Type:application/json" -H "Accept:application/json" http://localhost:8080/api/v1/foo/postFoo -d '{"strValue":"hello","intValue":123}'
=>
[INFO] .. BadlyConsumingRequestInputFilter => consume 10 chars for body: '{"strValue' .. then filterChain.doFilter(req,resp)

{"timestamp":"2021-03-19T07:55:04.757+0000","status":400,"error":"Bad Request","message":"JSON parse error: Cannot construct instance of `fr.an.tests.springboothttpfilter.dto.FooRequest` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value (':'); nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `fr.an.tests.springboothttpfilter.dto.FooRequest` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value (':')\n at [Source: (PushbackInputStream); line: 1, column: 1]","path":"/api/v1/foo/postFoo"}


package fr.an.tests.webfluxservlerlog.ws;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

import fr.an.tests.webfluxservlerlog.svc.RecentLogService;
import reactor.core.publisher.Flux;

/**
 * Rest Controller for LogEvents, either using plain old Get/Post http Calls,
 * or using Server-Sent-Events polling http calls 
 *
 */
@RestController
@RequestMapping("/api/recentlog")

@CrossOrigin(origins = "http://localhost:4200")

public class RecentLogController {

	@Autowired
	private RecentLogService recentLogService;

	/**
	 * Get all in-memory recent logs
	 * 
	 * <PRE>
	 * curl -H 'Accept: application/json' http://localhost:8081/api/recentlog/all 
	 * </PRE>
	 */
	@GetMapping("/all")
	public List<LogEventDTO> listAll() {
		return recentLogService.listAll();
	}

	@GetMapping(path="/listByTracedId/{traceId}")
    public List<LogEventDTO> listByTraceId(
    		@PathVariable("traceId") String traceId) {
    	return recentLogService.listByTraceId(traceId);
	}

	@PostMapping(path="/filterBy")
    public List<LogEventDTO> filterBy(
    		@RequestBody LogCriteriaDTO req) {
		return recentLogService.filterBy(req);
    }

	// listen to Server-Sent-Events
	// ------------------------------------------------------------------------

	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'last-event-id=..' http://localhost:8081/recentlog/sse/forTrace/${traceId} 
	 * </PRE>
	 * ... get results:
	 * <PRE>
	 * id:15                                                                                                                                        
	 * event:log                                                                                                                                    
	 * data:{"threadName":"http-nio-8080-exec-2","timestamp":1558218822166,"logger":"fr.an.tests.webfluxservlerlog.ws.FooController","severity":"INF
	 * O","message":"hello 8","exception":null,"messageTemplate":"hello 8","argumentArray":null,"traceId":null,"userName":null,"traceRequest":null} 
	 *                                                                                                                                              
	 * id:16                                                                                                                                        
	 * event:log                                                                                                                                    
	 * data:{"threadName":"http-nio-8080-exec-2","timestamp":1558218823169,"logger":"fr.an.tests.webfluxservlerlog.ws.FooController","severity":"INF
	 * O","message":"hello 9","exception":null,"messageTemplate":"hello 9","argumentArray":null,"traceId":null,"userName":null,"traceRequest":null} 
	 * </PRE>
	 */
	@GetMapping(path = "/sse/all", produces = "text/event-stream")
    public Flux<ServerSentEvent<LogEventDTO>> listenAll(
    		@RequestHeader(name="last-event-id", required=false) String lastEventId) {
		return recentLogService.listenAll(lastEventId);
    }

	/**
	 * example to test:
	 * <PRE>
	 * curl -H 'last-event-id=..' http://localhost:8081/recentlog/sse/forTrace/${traceId} 
	 * </PRE>
	 * ... get results:
	 * <PRE>
	 * ..
	 * 
	 * id:16                                                                                                                                        
	 * event:log                                                                                                                                    
	 * data:{"threadName":"http-nio-8080-exec-2","timestamp":1558218823169,"logger":"fr.an.tests.webfluxservlerlog.ws.FooController","severity":"INF
	 * O","message":"hello 9","exception":null,"messageTemplate":"hello 9","argumentArray":null,"traceId":null,"userName":null,"traceRequest":null} 
	 * </PRE>
	 * 
	 */
	@GetMapping(path = "/sse/byTraceId/{traceId}", produces = "text/event-stream")
    public Flux<ServerSentEvent<LogEventDTO>> listenByTraceId(
    		@PathVariable("traceId") String traceId,
    		@RequestHeader(name="last-event-id", required=false) String lastEventId) {
		return recentLogService.listenByTraceId(lastEventId, traceId);
    }

	@PostMapping(path = "/sse/filterBy", produces = "text/event-stream")
    public Flux<ServerSentEvent<LogEventDTO>> listenByFilter(
    		@RequestBody LogCriteriaDTO req,
    		@RequestHeader(name="last-event-id", required=false) String lastEventId) {
		return recentLogService.listenByFilter(lastEventId, req);
    }

	// handle normal "Async timeout", to avoid logging warn messages every 30s per client...
	@ExceptionHandler(value = AsyncRequestTimeoutException.class)
	public String asyncTimeout(AsyncRequestTimeoutException e) {
		return null; // "SSE timeout..OK";
	}
}

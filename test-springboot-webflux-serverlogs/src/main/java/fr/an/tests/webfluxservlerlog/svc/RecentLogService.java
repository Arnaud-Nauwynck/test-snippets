package fr.an.tests.webfluxservlerlog.svc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.helpers.CyclicBuffer;
import fr.an.tests.webfluxservlerlog.logback.LogServiceAppender;
import fr.an.tests.webfluxservlerlog.ws.LogCriteriaDTO;
import fr.an.tests.webfluxservlerlog.ws.LogEventDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

@Service
public class RecentLogService {
	
	private Object lock = new Object();
	
	private CyclicBuffer<LogEventDTO> cyclicBuffer = new CyclicBuffer<>(1000);
	
	private String jvmUUID = UUID.randomUUID().toString();
	private int idGenerator = 1;
	
	private ReplayProcessor<ServerSentEvent<LogEventDTO>> replayProcessor = 
			ReplayProcessor.<ServerSentEvent<LogEventDTO>>create(1000);
		
	
	@PostConstruct
	public void init() {
		LogServiceAppender thisAppender = new LogServiceAppender(this);
		thisAppender.start();

		Logger rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
        rootLogger.addAppender(thisAppender);
	}
	
	// ------------------------------------------------------------------------

	public void append(LogEventDTO logEvent) {
		synchronized(lock) {
			// also add to cyclic buffer (simpler api for listAll ??)
			cyclicBuffer.add(logEvent);
		}
		
		// assign type+id to wrap as a SSE Event
		ServerSentEvent<LogEventDTO> sseEvent = ServerSentEvent.builder(logEvent)
				.event("log")
				.id(jvmUUID + "#" + idGenerator++)
				.build();
		replayProcessor.onNext(sseEvent);
	}
	
	// ------------------------------------------------------------------------

	public List<LogEventDTO> listAll() {
		//?? return replayProcessor.all(ssePredicateAll);
		synchronized(lock) {
			return cyclicBuffer.asList();
		}
	}
	
	public List<LogEventDTO> listFilterBy(Predicate<LogEventDTO> pred) {
		List<LogEventDTO> tmp = listAll();
		List<LogEventDTO> res = new ArrayList<>();
		for(LogEventDTO e : tmp) {
			if (pred.test(e)) {
				res.add(e);
			}
		}
		return res;
	}
	

	public Flux<ServerSentEvent<LogEventDTO>> listenByFilter(Predicate<ServerSentEvent<LogEventDTO>> pred) {
		return replayProcessor.filter(pred);
	}

	// helper methods for list in-memory
	// ------------------------------------------------------------------------

	private static Predicate<LogEventDTO> predicateForTraceId(String traceId) {
		return x -> traceId.contentEquals(x.traceId);
	}

	private Predicate<LogEventDTO> predicateForCrit(LogCriteriaDTO req) {
		return x -> {
//			if (req.)
			return true;
		};
	}
	
	public List<LogEventDTO> listByTraceId(String traceId) {
		return listFilterBy(predicateForTraceId(traceId));
	}
	
	public List<LogEventDTO> filterBy(LogCriteriaDTO req) {
		return listFilterBy(predicateForCrit(req));
	}

	// helper methods for listen to Server-Sent-Events
	// ------------------------------------------------------------------------

	public Flux<ServerSentEvent<LogEventDTO>> listenAll(String lastEventId) {
		return listenByFilter(ssePredicate(lastEventId));
	}
	
	public Flux<ServerSentEvent<LogEventDTO>> listenByTraceId(int lastEventId, String traceId) {
		return listenByFilter(ssePredicate(lastEventId, predicateForTraceId(traceId)));
	}
	
	public Flux<ServerSentEvent<LogEventDTO>> listenByFilter(int lastEventId, LogCriteriaDTO req) {
		return listenByFilter(ssePredicate(lastEventId, predicateForCrit(req)));
	}

	private static final Predicate<ServerSentEvent<LogEventDTO>> sseAll = x -> true;
	
	private static Predicate<ServerSentEvent<LogEventDTO>> ssePredicate(String lastEventId) {
		if (lastEventId == null) {
			return sseAll;
		}
		int sep = lastEventId.lastIndexOf("#");
		if (sep == -1) {
			return sseAll;
		}
		String lastUuid = lastEventId.substring(0, sep);
		if (lastUuid == null) {
			return sseAll;
		}
		int lastEventIdVal = Integer.parseInt(lastEventId.substring(sep+1, lastEventId.length()));
		return x -> {
			String eventId = x.id();
			int eventSep = eventId.lastIndexOf("#");
			String evtUuid = eventId.substring(0, eventSep);
			if (!lastUuid.contentEquals(evtUuid)) {
				return true;
			}
			int eventIdVal = Integer.parseInt(eventId.substring(eventSep+1, eventId.length()));
			boolean res = eventIdVal > lastEventIdVal;
			return res;
		};
	}
	
	private static Predicate<ServerSentEvent<LogEventDTO>> ssePredicate(int lastEventId, Predicate<LogEventDTO> pred) {
		return x -> Integer.parseInt(x.id()) > lastEventId 
				&& pred.test(x.data());
	}

	
}

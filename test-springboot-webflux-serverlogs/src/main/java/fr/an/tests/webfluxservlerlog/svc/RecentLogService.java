package fr.an.tests.webfluxservlerlog.svc;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import fr.an.tests.webfluxservlerlog.logback.LogServiceAppender;
import fr.an.tests.webfluxservlerlog.util.Ls;
import fr.an.tests.webfluxservlerlog.ws.LogCriteriaDTO;
import fr.an.tests.webfluxservlerlog.ws.LogEventDTO;
import lombok.Value;
import lombok.val;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ReplayProcessor;

@Service
public class RecentLogService {

	private String jvmUUID = UUID.randomUUID().toString();
	private int idGenerator = 1;

	private ReplayProcessor<ServerSentEvent<LogEventDTO>> replayProcessor = ReplayProcessor
			.<ServerSentEvent<LogEventDTO>>create(1000);

	@PostConstruct
	public void init() {
		LogServiceAppender thisAppender = new LogServiceAppender(this);
		thisAppender.start();

		Logger rootLogger = (Logger) LoggerFactory.getLogger("ROOT");
		rootLogger.addAppender(thisAppender);
	}

	// ------------------------------------------------------------------------

	public void append(LogEventDTO logEvent) {
		// assign type+id to wrap as a SSE Event
		ServerSentEvent<LogEventDTO> sseEvent = ServerSentEvent.builder(logEvent).event("log")
				.id(jvmUUID + "#" + idGenerator++).build();
		try {
			replayProcessor.onNext(sseEvent);
		} catch (Exception ex) {
			System.err.println("Should not occurr.. do no re-log logging error.. " + ex.getMessage());
		}
	}

	// ------------------------------------------------------------------------

	public List<LogEventDTO> listAll() {
		List<ServerSentEvent<LogEventDTO>> sseEvents = replayProcessor.collectList().block(Duration.ZERO);
		return Ls.map(sseEvents, e -> e.data());
	}

	public List<LogEventDTO> listFilterBy(Predicate<LogEventDTO> pred) {
		return Ls.filter(listAll(), pred);
	}

	public Flux<ServerSentEvent<LogEventDTO>> listenByFilter(Predicate<ServerSentEvent<LogEventDTO>> pred) {
		return replayProcessor.filter(pred);
	}

	// helper methods for list in-memory
	// ------------------------------------------------------------------------

	private static Predicate<LogEventDTO> predicateForTraceId(String traceId) {
		return x -> traceId.contentEquals(x.traceId);
	}

	private Predicate<LogEventDTO> predicateForCrit(final LogCriteriaDTO req) {
		final Pattern reqMsgPattern = (req.msgPattern != null) ? Pattern.compile(req.msgPattern) : null;
		final Pattern reqMsgTemplatePattern = (req.msgTemplatePattern != null) ? Pattern.compile(req.msgTemplatePattern)
				: null;
		Predicate<LogEventDTO> res = evt -> {
			if (req.traceId != null && !req.traceId.equals(evt.traceId)) {
				return false;
			}
			if (req.username != null && !req.username.equals(evt.userName)) {
				return false;
			}
			if (req.fromTimestamp != 0 && evt.timestamp < req.fromTimestamp) {
				return false;
			}
			if (req.toTimestamp != 0 && evt.timestamp > req.toTimestamp) {
				return false;
			}
			if (req.severity != null && !Level.toLevel(evt.severity).isGreaterOrEqual(Level.toLevel(req.severity))) {
				return false;
			}
			if (reqMsgPattern != null && !reqMsgPattern.matcher(evt.message).matches()) {
				return false;
			}
			if (reqMsgTemplatePattern != null && !reqMsgPattern.matcher(evt.messageTemplate).matches()) {
				return false;
			}

			return true;
		};

		if (req.limit != 0) {
			// wrap with statefull (not-reusable) predicate for max limit (?!)
			final Predicate<LogEventDTO> delegatePred = res;
			res = new Predicate<LogEventDTO>() {
				private int statefullCountAccepted = 0;

				@Override
				public boolean test(LogEventDTO event) {
					if (this.statefullCountAccepted > req.limit) {
						return false;
					}
					if (delegatePred.test(event)) {
						this.statefullCountAccepted++;
						return true;
					}
					return false;
				}
			};
		}

		return res;
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

	public Flux<ServerSentEvent<LogEventDTO>> listenByTraceId(String lastEventId, String traceId) {
		return listenByFilter(ssePredicate(lastEventId, predicateForTraceId(traceId)));
	}

	public Flux<ServerSentEvent<LogEventDTO>> listenByFilter(String lastEventId, LogCriteriaDTO req) {
		return listenByFilter(ssePredicate(lastEventId, predicateForCrit(req)));
	}

	@Value
	public static class UuidEventId {
		public final String jvmUuid;
		public final int sequence;

		public static UuidEventId parse(String text) {
			if (text == null) {
				return null;
			}
			int sep = text.lastIndexOf("#");
			if (sep == -1) {
				return null;
			}
			String uuid = text.substring(0, sep);
			int sequence = Integer.parseInt(text.substring(sep + 1, text.length()));
			return new UuidEventId(uuid, sequence);
		}

		public boolean isDiffJvmOrBefore(UuidEventId eventId) {
			if (eventId == null) {
				return false;
			}
			if (!jvmUuid.equals(eventId.jvmUuid)) {
				return true;
			}
			return this.sequence < eventId.sequence;
		}

		public boolean isDiffJvmOrBefore(String eventId) {
			// equivalent if valid to: return isDiffJvmOrBefore(parse(sseEventId));
			if (eventId == null) {
				return false; // should not occur
			}
			if (!eventId.startsWith(jvmUuid)) {
				return true;
			}
			int sep = eventId.lastIndexOf("#");
			if (sep == -1) {
				return false;
			}
			int sseEventSequence = Integer.parseInt(eventId.substring(sep + 1, eventId.length()));
			return this.sequence < sseEventSequence;
		}
	}

	private static final Predicate<ServerSentEvent<LogEventDTO>> sseAll = x -> true;

	private static Predicate<ServerSentEvent<LogEventDTO>> ssePredicate(String lastEventId) {
		if (lastEventId == null) {
			return sseAll;
		}
		val last = UuidEventId.parse(lastEventId);
		if (last == null) {
			return sseAll;
		}
		return sseEvent -> last.isDiffJvmOrBefore(sseEvent.id());
	}

	private static Predicate<ServerSentEvent<LogEventDTO>> ssePredicate(String lastEventId,
			Predicate<LogEventDTO> pred) {
		val last = UuidEventId.parse(lastEventId);
		return sseEvent -> last.isDiffJvmOrBefore(sseEvent.id()) && pred.test(sseEvent.data());
	}

}

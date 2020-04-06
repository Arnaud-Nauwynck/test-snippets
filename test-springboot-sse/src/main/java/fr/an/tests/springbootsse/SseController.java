package fr.an.tests.springbootsse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Controller
@RequestMapping("/sse")
public class SseController {

	Map<String,SseEmitter> topicSseEmiters = new HashMap<>();
	int idGen = 1;
	
	@GetMapping("/infinite")
	public SseEmitter streamSseMvc() {
		SseEmitter emitter = new SseEmitter();
		ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
		sseMvcExecutor.execute(() -> {
			try {
				for (int i = 0; true; i++) {
					SseEventBuilder event = SseEmitter.event().data("SSE MVC - "
					// + LocalTime.now().toString()
					).id(String.valueOf(i)).name("sse event - mvc");
					emitter.send(event);
					Thread.sleep(1000);
				}
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
	}
	
	@GetMapping("/topic")
	public SseEmitter getTopicEmitter(
			@RequestParam("topic") String topic) {
		SseEmitter res = getOrCreateTopic(topic);
		return res;
	}

	@PostMapping("/sendToTopic")
	public void sendToTopic(
			@RequestParam("topic") String topic, 
			@RequestParam("msg") String msg) throws IOException {
		SseEmitter emitter = getOrCreateTopic(topic);
		SseEventBuilder event = SseEmitter.event().data(msg)
				.id(String.valueOf(idGen++));
		emitter.send(event);
	}

	private SseEmitter getOrCreateTopic(String topic) {
		SseEmitter res = topicSseEmiters.get(topic);
		if (res == null) {
			res = new SseEmitter();
			topicSseEmiters.put(topic, res);
		}
		return res;
	}
	
}

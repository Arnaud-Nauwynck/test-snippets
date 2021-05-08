package fr.an.test.atomix.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="app")
public class AppConfigParam {

	private AtomicConfigParam atomix;
	
	@Data
	public static class AtomicConfigParam {
		private String memberId;
		private String hostname;
		private int port;
		private List<NodeConfigParam> bootstrapNodes;
	}
	@Data
	public static class NodeConfigParam {
		private String memberId;
		private String hostname;
		private int port;
	}
}

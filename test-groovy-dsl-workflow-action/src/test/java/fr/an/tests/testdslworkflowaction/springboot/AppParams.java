package fr.an.tests.testdslworkflowaction.springboot;


import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
public class AppParams {

	private String prop1;

	private List<Map<String, Object>> workflows;

	public AppParams() {
	}
	
	public void setProp1(String prop1) {
		this.prop1 = prop1;
	}

	public void setWorkflows(List<Map<String, Object>> workflows) {
		this.workflows = workflows;
	}

	
}

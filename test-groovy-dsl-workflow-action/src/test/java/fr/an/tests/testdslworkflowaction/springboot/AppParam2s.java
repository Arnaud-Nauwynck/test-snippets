package fr.an.tests.testdslworkflowaction.springboot;


import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app")
@Getter @Setter
public class AppParam2s {

	private String prop1;

	private List<Map<String, Object>> workflows;

}

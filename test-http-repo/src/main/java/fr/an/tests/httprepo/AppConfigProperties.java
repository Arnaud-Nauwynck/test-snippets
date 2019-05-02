package fr.an.tests.httprepo;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "repo")
public class AppConfigProperties {

	String baseDir;
	
	List<String> resp404;

}

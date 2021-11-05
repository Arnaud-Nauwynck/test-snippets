package fr.an.tests.reverseyarn.dto.rmws;

import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.webapp.dao.QueueConfigInfo
 */
@Data
public class QueueConfigInfo {

	@XmlElement(name="queue-name")
	private String queue;
	  
	private Map<String,String> params;
	  
}

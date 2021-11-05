package fr.an.tests.reverseyarn.dto.rmws;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.webapp.dao.SchedConfUpdateInfo
 */
@Data
public class SchedConfUpdateInfo {

	@XmlElement(name = "add-queue")
	private List<QueueConfigInfo> addQueueInfo;

	@XmlElement(name = "remove-queue")
	private List<String> removeQueueInfo;

	@XmlElement(name = "update-queue")
	private List<QueueConfigInfo> updateQueueInfo;

	private Map<String, String> global;

}

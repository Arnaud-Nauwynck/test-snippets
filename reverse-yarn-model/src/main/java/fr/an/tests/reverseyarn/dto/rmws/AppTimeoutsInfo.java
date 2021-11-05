package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.AppTimeoutsInfo
 */
@Data
public class AppTimeoutsInfo {

	@XmlElement(name = "timeout")
	private ArrayList<AppTimeoutInfo> timeouts = new ArrayList<AppTimeoutInfo>();

}

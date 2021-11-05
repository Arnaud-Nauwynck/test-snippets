package fr.an.tests.reverseyarn.dto.rmws;

import java.net.URI;

import javax.xml.bind.annotation.XmlElement;

import fr.an.tests.reverseyarn.dto.app.LocalResourceType;
import fr.an.tests.reverseyarn.dto.app.LocalResourceVisibility;
import lombok.Data;

/**
 * org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.LocalResourceInfo
 */
@Data
public class LocalResourceInfo {

	@XmlElement(name = "resource")
	URI url;

	LocalResourceType type;
	
	LocalResourceVisibility visibility;
	
	long size;
	long timestamp;
	String pattern;

}

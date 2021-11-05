package fr.an.tests.reverseyarn.dto.rmws;

import java.util.ArrayList;

import lombok.Data;

@Data
public class NodeAttributesInfo {

	// @XmlElement(name = "nodeAttributeInfo")
	private ArrayList<NodeAttributeInfo> nodeAttributesInfo = new ArrayList<>();

}

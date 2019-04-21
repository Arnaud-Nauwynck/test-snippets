package fr.an.tests.reverseyarn.dto;

import lombok.Value;

@Value
public class NodeLabel {

	public static final String DEFAULT_NODE_LABEL_PARTITION = "<DEFAULT_PARTITION>";
	public static final String NODE_LABEL_EXPRESSION_NOT_SET = "<Not set>";
	public static final boolean DEFAULT_NODE_LABEL_EXCLUSIVITY = true;

	public final String name;
	public final boolean exclusivity;

}

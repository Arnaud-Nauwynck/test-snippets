package fr.an.tests.reverseyarn.dto;

import lombok.Value;

@Value
public class NodeId {
	public final String host;
	public final int port;
}

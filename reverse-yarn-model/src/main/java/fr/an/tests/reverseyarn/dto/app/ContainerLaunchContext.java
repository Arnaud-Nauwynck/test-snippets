package fr.an.tests.reverseyarn.dto.app;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class ContainerLaunchContext {

	public ByteBuffer tokens;
	public ByteBuffer tokensConf;
	public Map<String, LocalResource> localResources;
	public Map<String, ByteBuffer> serviceData;
	public Map<String, String> environment;
	public List<String> commands;
	public Map<ApplicationAccessType, String> applicationACLs;
	public ContainerRetryContext containerRetryContext;

}

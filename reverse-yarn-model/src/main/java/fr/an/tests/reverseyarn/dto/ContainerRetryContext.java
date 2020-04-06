package fr.an.tests.reverseyarn.dto;

import java.util.Set;

import lombok.Data;

@Data
public class ContainerRetryContext {

	public static final int RETRY_FOREVER = -1;
	public static final int RETRY_INVALID = -1000;

	public ContainerRetryPolicy retryPolicy;
	public Set<Integer> errorCodes;
	public int maxRetries;
	public int retryInterval;
	public long failuresValidityInterval;

}

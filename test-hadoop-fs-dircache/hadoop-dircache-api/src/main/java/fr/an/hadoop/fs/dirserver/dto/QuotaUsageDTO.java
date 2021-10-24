package fr.an.hadoop.fs.dirserver.dto;

import lombok.Data;

/**
 * json DTO for QuotaUsage
 */
@Data
public class QuotaUsageDTO {

	private long fileAndDirectoryCount;
	// Make the followings protected so that
	// deprecated ContentSummary constructor can use them.
	private long quota;
	private long spaceConsumed;
	private long spaceQuota;
	private long[] typeConsumed;
	private long[] typeQuota;

}

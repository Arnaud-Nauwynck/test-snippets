package fr.an.hadoop.fs.dirserver.dto;

import lombok.Data;

/**
 * json DTO for hadoop ContentSummary
 */
@Data
public class ContentSummaryDTO extends QuotaUsageDTO {

	private long length;
	private long fileCount;
	private long directoryCount;
	// These fields are to track the snapshot-related portion of the values.
	private long snapshotLength;
	private long snapshotFileCount;
	private long snapshotDirectoryCount;
	private long snapshotSpaceConsumed;
	private String erasureCodingPolicy;
		  
}

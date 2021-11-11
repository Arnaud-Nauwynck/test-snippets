package fr.an.hadoop.ambarimetrics.ws.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * see org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse
 * https://github.com/naver/hadoop/blob/9e1123d0a983297d48ec53f786731e52c498d6b4/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-api/src/main/java/org/apache/hadoop/yarn/api/records/timeline/TimelinePutResponse.java
 */
@Data
public class TimelinePutResponseDTO {

	private List<TimelinePutErrorDTO> errors = new ArrayList<>();
	
	/**
	 * see org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse.TimelinePutError
	 */
	@Data
	public static class TimelinePutErrorDTO {
		/**
	     * Error code returned when no start time can be found when putting an
	     * entity. This occurs when the entity does not already exist in the store
	     * and it is put with no start time or events specified.
	     */
	    public static final int NO_START_TIME = 1;
	    /**
	     * Error code returned if an IOException is encountered when putting an
	     * entity.
	     */
	    public static final int IO_EXCEPTION = 2;

	    /**
	     * Error code returned if the user specifies the timeline system reserved
	     * filter key
	     */
	    public static final int SYSTEM_FILTER_CONFLICT = 3;

	    /**
	     * Error code returned if the user is denied to access the timeline data
	     */
	    public static final int ACCESS_DENIED = 4;

	    /**
	     * Error code returned if the entity doesn't have an valid domain ID
	     */
	    public static final int NO_DOMAIN = 5;

	    /**
	     * Error code returned if the user is denied to relate the entity to another
	     * one in different domain
	     */
	    public static final int FORBIDDEN_RELATION = 6;

	    private String entityId;
	    private String entityType;
	    private int errorCode;
	}
}

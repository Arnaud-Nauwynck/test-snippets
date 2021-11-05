package fr.an.tests.reverseyarn.dto.rmws;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;

/**
 * org.apache.hadoop.yarn.api.records.ResourceInformation
 */
@Data
public class ResourceInformation {

	  private String name;
	  private String units;
	  private ResourceTypes resourceType;
	  private long value;
	  private long minimumAllocation;
	  private long maximumAllocation;
	  private Set<String> tags = new HashSet<>();
	  private Map<String, String> attributes = new HashMap<>();
	  
	  // Known resource types
	  public static final String MEMORY_URI = "memory-mb";
	  public static final String VCORES_URI = "vcores";
	  public static final String GPU_URI = "yarn.io/gpu";
	  public static final String FPGA_URI = "yarn.io/fpga";
}

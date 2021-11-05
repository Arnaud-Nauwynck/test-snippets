package fr.an.tests.reverseyarn.dto.app;

import lombok.Data;

@Data
public class YarnClusterMetrics {

	  public int numNodeManagers;
	  public int numDecommissionedNodeManagers;
	  public int numActiveNodeManagers;
	  public int numLostNodeManagers;
	  public int numUnhealthyNodeManagers;
	  public int numRebootedNodeManagers;

}

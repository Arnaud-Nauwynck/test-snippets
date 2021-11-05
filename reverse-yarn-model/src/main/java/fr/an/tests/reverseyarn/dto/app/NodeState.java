package fr.an.tests.reverseyarn.dto.app;

public enum NodeState {
  NEW, 
  RUNNING, 
  UNHEALTHY, 
  DECOMMISSIONED, 
  LOST, 
  REBOOTED,
  DECOMMISSIONING,
  SHUTDOWN;
}

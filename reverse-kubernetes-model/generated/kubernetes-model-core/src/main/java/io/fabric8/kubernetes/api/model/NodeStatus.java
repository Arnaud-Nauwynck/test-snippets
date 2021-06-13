
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeStatus implements KubernetesResource
{

    public List<NodeAddress> addresses = new ArrayList<NodeAddress>();
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> allocatable;
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> capacity;
    public List<NodeCondition> conditions = new ArrayList<NodeCondition>();
    public NodeConfigStatus config;
    public NodeDaemonEndpoints daemonEndpoints;
    public List<ContainerImage> images = new ArrayList<ContainerImage>();
    public NodeSystemInfo nodeInfo;
    public java.lang.String phase;
    public List<AttachedVolume> volumesAttached = new ArrayList<AttachedVolume>();
    public List<java.lang.String> volumesInUse = new ArrayList<java.lang.String>();

}

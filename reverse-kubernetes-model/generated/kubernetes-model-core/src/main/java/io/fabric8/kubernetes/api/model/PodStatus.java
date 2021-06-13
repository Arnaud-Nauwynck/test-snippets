
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodStatus implements KubernetesResource
{

    public List<PodCondition> conditions = new ArrayList<PodCondition>();
    public List<ContainerStatus> containerStatuses = new ArrayList<ContainerStatus>();
    public List<ContainerStatus> ephemeralContainerStatuses = new ArrayList<ContainerStatus>();
    public java.lang.String hostIP;
    public List<ContainerStatus> initContainerStatuses = new ArrayList<ContainerStatus>();
    public java.lang.String message;
    public java.lang.String nominatedNodeName;
    public java.lang.String phase;
    public java.lang.String podIP;
    public List<PodIP> podIPs = new ArrayList<PodIP>();
    public java.lang.String qosClass;
    public java.lang.String reason;
    public String startTime;

}

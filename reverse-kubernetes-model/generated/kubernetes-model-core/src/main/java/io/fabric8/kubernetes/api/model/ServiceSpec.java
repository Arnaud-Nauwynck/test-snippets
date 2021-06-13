
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ServiceSpec implements KubernetesResource
{

    public boolean allocateLoadBalancerNodePorts;
    public java.lang.String clusterIP;
    public List<java.lang.String> clusterIPs = new ArrayList<java.lang.String>();
    public List<java.lang.String> externalIPs = new ArrayList<java.lang.String>();
    public java.lang.String externalName;
    public java.lang.String externalTrafficPolicy;
    public int healthCheckNodePort;
    public java.lang.String internalTrafficPolicy;
    public List<java.lang.String> ipFamilies = new ArrayList<java.lang.String>();
    public java.lang.String ipFamilyPolicy;
    public java.lang.String loadBalancerClass;
    public java.lang.String loadBalancerIP;
    public List<java.lang.String> loadBalancerSourceRanges = new ArrayList<java.lang.String>();
    public List<ServicePort> ports = new ArrayList<ServicePort>();
    public boolean publishNotReadyAddresses;
    public Map<String, String> selector;
    public java.lang.String sessionAffinity;
    public SessionAffinityConfig sessionAffinityConfig;
    public List<java.lang.String> topologyKeys = new ArrayList<java.lang.String>();
    public java.lang.String type;

}

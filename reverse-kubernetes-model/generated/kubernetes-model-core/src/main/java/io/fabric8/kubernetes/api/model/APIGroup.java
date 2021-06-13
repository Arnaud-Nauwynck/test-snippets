
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class APIGroup implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "APIGroup";
    public String name;
    public GroupVersionForDiscovery preferredVersion;
    public List<ServerAddressByClientCIDR> serverAddressByClientCIDRs = new ArrayList<ServerAddressByClientCIDR>();
    public List<GroupVersionForDiscovery> versions = new ArrayList<GroupVersionForDiscovery>();

}


package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LoadBalancerIngress implements KubernetesResource
{

    public String hostname;
    public String ip;
    public List<PortStatus> ports = new ArrayList<PortStatus>();

}

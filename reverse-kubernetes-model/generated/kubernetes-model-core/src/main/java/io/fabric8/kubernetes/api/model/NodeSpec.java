
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeSpec implements KubernetesResource
{

    public NodeConfigSource configSource;
    public String externalID;
    public String podCIDR;
    public List<String> podCIDRs = new ArrayList<String>();
    public String providerID;
    public List<Taint> taints = new ArrayList<Taint>();
    public boolean unschedulable;

}

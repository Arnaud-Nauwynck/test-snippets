
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LoadBalancerStatus implements KubernetesResource
{

    public List<LoadBalancerIngress> ingress = new ArrayList<LoadBalancerIngress>();

}

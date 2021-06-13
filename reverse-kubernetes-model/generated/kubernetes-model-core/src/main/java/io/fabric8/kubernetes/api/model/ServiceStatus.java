
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ServiceStatus implements KubernetesResource
{

    public List<Condition> conditions = new ArrayList<Condition>();
    public LoadBalancerStatus loadBalancer;

}

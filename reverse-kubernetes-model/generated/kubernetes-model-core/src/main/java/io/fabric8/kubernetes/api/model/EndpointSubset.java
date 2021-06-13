
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class EndpointSubset implements KubernetesResource
{

    public List<EndpointAddress> addresses = new ArrayList<EndpointAddress>();
    public List<EndpointAddress> notReadyAddresses = new ArrayList<EndpointAddress>();
    public List<EndpointPort> ports = new ArrayList<EndpointPort>();

}

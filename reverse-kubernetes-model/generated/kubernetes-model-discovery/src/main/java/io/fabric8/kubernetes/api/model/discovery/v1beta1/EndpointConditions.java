
package io.fabric8.kubernetes.api.model.discovery.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class EndpointConditions implements KubernetesResource
{

    public boolean ready;
    public boolean serving;
    public boolean terminating;

}

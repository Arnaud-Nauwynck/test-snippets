
package io.fabric8.kubernetes.api.model.discovery.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class EndpointPort implements KubernetesResource
{

    public String appProtocol;
    public String name;
    public int port;
    public String protocol;

}

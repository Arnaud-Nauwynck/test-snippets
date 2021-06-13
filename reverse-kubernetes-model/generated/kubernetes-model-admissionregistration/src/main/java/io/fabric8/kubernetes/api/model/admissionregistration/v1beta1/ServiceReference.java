
package io.fabric8.kubernetes.api.model.admissionregistration.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class ServiceReference implements KubernetesResource
{

    public String name;
    public String namespace;
    public String path;
    public int port;

}

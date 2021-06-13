
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.TypedLocalObjectReference;

@Generated("jsonschema2pojo")
public class IngressBackend implements KubernetesResource
{

    public TypedLocalObjectReference resource;
    public String serviceName;
    public IntOrString servicePort;

}

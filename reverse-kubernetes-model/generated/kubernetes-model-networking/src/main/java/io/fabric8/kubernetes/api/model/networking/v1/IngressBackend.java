
package io.fabric8.kubernetes.api.model.networking.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.TypedLocalObjectReference;

@Generated("jsonschema2pojo")
public class IngressBackend implements KubernetesResource
{

    public TypedLocalObjectReference resource;
    public IngressServiceBackend service;

}


package io.fabric8.kubernetes.api.model.networking.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class Ingress implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "networking.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Ingress";
    public ObjectMeta metadata;
    public IngressSpec spec;
    public IngressStatus status;

}

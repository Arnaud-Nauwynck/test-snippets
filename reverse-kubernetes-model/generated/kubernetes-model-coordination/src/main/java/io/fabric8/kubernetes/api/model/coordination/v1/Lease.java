
package io.fabric8.kubernetes.api.model.coordination.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class Lease implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "coordination.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Lease";
    public ObjectMeta metadata;
    public LeaseSpec spec;

}

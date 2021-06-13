
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.DeleteOptions;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class Eviction implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "policy/v1beta1";
    public DeleteOptions deleteOptions;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Eviction";
    public ObjectMeta metadata;

}

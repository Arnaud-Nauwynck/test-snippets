
package io.fabric8.kubernetes.api.model.scheduling.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class PriorityClass implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "scheduling.k8s.io/v1";
    public String description;
    public boolean globalDefault;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PriorityClass";
    public ObjectMeta metadata;
    public String preemptionPolicy;
    public int value;

}

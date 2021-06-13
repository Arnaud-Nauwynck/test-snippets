
package io.fabric8.kubernetes.api.model.autoscaling.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class Scale implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "autoscaling/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Scale";
    public ObjectMeta metadata;
    public ScaleSpec spec;
    public ScaleStatus status;

}

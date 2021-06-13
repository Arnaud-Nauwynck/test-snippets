
package io.fabric8.kubernetes.api.model.autoscaling.v2beta2;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscaler implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "autoscaling/v2beta2";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "HorizontalPodAutoscaler";
    public ObjectMeta metadata;
    public HorizontalPodAutoscalerSpec spec;
    public HorizontalPodAutoscalerStatus status;

}

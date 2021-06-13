
package io.fabric8.kubernetes.api.model.autoscaling.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscalerList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscaler>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "autoscaling/v1";
    public List<io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscaler> items = new ArrayList<io.fabric8.kubernetes.api.model.autoscaling.v1.HorizontalPodAutoscaler>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "HorizontalPodAutoscalerList";
    public ListMeta metadata;

}


package io.fabric8.kubernetes.api.model.autoscaling.v2beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HorizontalPodAutoscalerSpec implements KubernetesResource
{

    public int maxReplicas;
    public List<MetricSpec> metrics = new ArrayList<MetricSpec>();
    public int minReplicas;
    public CrossVersionObjectReference scaleTargetRef;

}

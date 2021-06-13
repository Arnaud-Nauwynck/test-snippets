
package io.fabric8.kubernetes.api.model.flowcontrol.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class QueuingConfiguration implements KubernetesResource
{

    public int handSize;
    public int queueLengthLimit;
    public int queues;

}


package io.fabric8.kubernetes.api.model.flowcontrol.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class LimitResponse implements KubernetesResource
{

    public QueuingConfiguration queuing;
    public String type;

}

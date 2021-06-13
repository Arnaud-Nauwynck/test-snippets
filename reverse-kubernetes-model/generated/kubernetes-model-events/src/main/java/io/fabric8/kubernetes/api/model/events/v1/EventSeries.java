
package io.fabric8.kubernetes.api.model.events.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.MicroTime;

@Generated("jsonschema2pojo")
public class EventSeries implements KubernetesResource
{

    public int count;
    public MicroTime lastObservedTime;

}

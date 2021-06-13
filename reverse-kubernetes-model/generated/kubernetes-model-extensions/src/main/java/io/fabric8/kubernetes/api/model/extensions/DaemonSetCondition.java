
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class DaemonSetCondition implements KubernetesResource
{

    public String lastTransitionTime;
    public java.lang.String message;
    public java.lang.String reason;
    public java.lang.String status;
    public java.lang.String type;

}

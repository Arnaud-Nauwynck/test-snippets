
package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class DeploymentCondition implements KubernetesResource
{

    public String lastTransitionTime;
    public String lastUpdateTime;
    public java.lang.String message;
    public java.lang.String reason;
    public java.lang.String status;
    public java.lang.String type;

}

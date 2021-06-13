
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class DeploymentStrategy implements KubernetesResource
{

    public RollingUpdateDeployment rollingUpdate;
    public String type;

}

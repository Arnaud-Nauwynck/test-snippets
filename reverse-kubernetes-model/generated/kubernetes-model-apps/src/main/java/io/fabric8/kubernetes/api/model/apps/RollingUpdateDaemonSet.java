
package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class RollingUpdateDaemonSet implements KubernetesResource
{

    public IntOrString maxSurge;
    public IntOrString maxUnavailable;

}


package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class StatefulSetUpdateStrategy implements KubernetesResource
{

    public RollingUpdateStatefulSetStrategy rollingUpdate;
    public String type;

}

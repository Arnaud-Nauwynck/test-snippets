
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.SELinuxOptions;

@Generated("jsonschema2pojo")
public class SELinuxStrategyOptions implements KubernetesResource
{

    public String rule;
    public SELinuxOptions seLinuxOptions;

}

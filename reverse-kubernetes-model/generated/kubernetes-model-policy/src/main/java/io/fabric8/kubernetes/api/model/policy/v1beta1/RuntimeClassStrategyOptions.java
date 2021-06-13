
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class RuntimeClassStrategyOptions implements KubernetesResource
{

    public List<String> allowedRuntimeClassNames = new ArrayList<String>();
    public String defaultRuntimeClassName;

}

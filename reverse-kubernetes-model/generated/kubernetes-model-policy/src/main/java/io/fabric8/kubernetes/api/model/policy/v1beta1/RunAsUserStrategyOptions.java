
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class RunAsUserStrategyOptions implements KubernetesResource
{

    public List<IDRange> ranges = new ArrayList<IDRange>();
    public String rule;

}

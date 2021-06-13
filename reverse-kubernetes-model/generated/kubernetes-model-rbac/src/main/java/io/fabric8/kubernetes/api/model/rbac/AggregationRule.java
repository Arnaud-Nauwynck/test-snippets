
package io.fabric8.kubernetes.api.model.rbac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;

@Generated("jsonschema2pojo")
public class AggregationRule implements KubernetesResource
{

    public List<LabelSelector> clusterRoleSelectors = new ArrayList<LabelSelector>();

}

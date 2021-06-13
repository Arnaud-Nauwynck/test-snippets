
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeAffinity implements KubernetesResource
{

    public List<PreferredSchedulingTerm> preferredDuringSchedulingIgnoredDuringExecution = new ArrayList<PreferredSchedulingTerm>();
    public NodeSelector requiredDuringSchedulingIgnoredDuringExecution;

}

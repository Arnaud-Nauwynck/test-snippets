
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeSelector implements KubernetesResource
{

    public List<NodeSelectorTerm> nodeSelectorTerms = new ArrayList<NodeSelectorTerm>();

}

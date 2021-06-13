
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeSelectorTerm implements KubernetesResource
{

    public List<NodeSelectorRequirement> matchExpressions = new ArrayList<NodeSelectorRequirement>();
    public List<NodeSelectorRequirement> matchFields = new ArrayList<NodeSelectorRequirement>();

}

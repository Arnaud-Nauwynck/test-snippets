
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodAffinityTerm implements KubernetesResource
{

    public LabelSelector labelSelector;
    public LabelSelector namespaceSelector;
    public List<String> namespaces = new ArrayList<String>();
    public String topologyKey;

}

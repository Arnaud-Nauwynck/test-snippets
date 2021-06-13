
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NodeSelectorRequirement implements KubernetesResource
{

    public String key;
    public String operator;
    public List<String> values = new ArrayList<String>();

}

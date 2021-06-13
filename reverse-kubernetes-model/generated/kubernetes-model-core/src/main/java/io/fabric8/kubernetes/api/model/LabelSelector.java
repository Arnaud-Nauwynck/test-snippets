
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LabelSelector implements KubernetesResource
{

    public List<LabelSelectorRequirement> matchExpressions = new ArrayList<LabelSelectorRequirement>();
    public Map<String, String> matchLabels;

}

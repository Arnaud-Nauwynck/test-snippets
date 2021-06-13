
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class TopologySelectorLabelRequirement implements KubernetesResource
{

    public String key;
    public List<String> values = new ArrayList<String>();

}

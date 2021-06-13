
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ScopedResourceSelectorRequirement implements KubernetesResource
{

    public String operator;
    public String scopeName;
    public List<String> values = new ArrayList<String>();

}

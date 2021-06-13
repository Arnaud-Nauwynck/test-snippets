
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class NamespaceStatus implements KubernetesResource
{

    public List<NamespaceCondition> conditions = new ArrayList<NamespaceCondition>();
    public String phase;

}

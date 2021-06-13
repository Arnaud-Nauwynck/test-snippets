
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ResourceQuotaSpec implements KubernetesResource
{

    public Map<String, Quantity> hard;
    public ScopeSelector scopeSelector;
    public List<java.lang.String> scopes = new ArrayList<java.lang.String>();

}

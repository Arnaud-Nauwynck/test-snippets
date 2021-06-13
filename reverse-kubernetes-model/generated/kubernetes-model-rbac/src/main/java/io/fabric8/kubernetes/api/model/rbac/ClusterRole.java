
package io.fabric8.kubernetes.api.model.rbac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class ClusterRole implements HasMetadata
{

    public AggregationRule aggregationRule;
    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "rbac.authorization.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ClusterRole";
    public ObjectMeta metadata;
    public List<PolicyRule> rules = new ArrayList<PolicyRule>();

}

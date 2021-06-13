
package io.fabric8.kubernetes.api.model.policy.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class PodSecurityPolicy implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "policy/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PodSecurityPolicy";
    public ObjectMeta metadata;
    public PodSecurityPolicySpec spec;

}

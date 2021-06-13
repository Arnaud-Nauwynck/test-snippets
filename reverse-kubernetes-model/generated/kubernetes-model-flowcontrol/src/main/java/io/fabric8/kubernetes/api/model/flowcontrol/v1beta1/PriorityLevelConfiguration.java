
package io.fabric8.kubernetes.api.model.flowcontrol.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class PriorityLevelConfiguration implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "flowcontrol.apiserver.k8s.io/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PriorityLevelConfiguration";
    public ObjectMeta metadata;
    public PriorityLevelConfigurationSpec spec;
    public PriorityLevelConfigurationStatus status;

}

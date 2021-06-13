
package io.fabric8.kubernetes.api.model.node.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class RuntimeClass implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "node.k8s.io/v1beta1";
    public String handler;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "RuntimeClass";
    public ObjectMeta metadata;
    public Overhead overhead;
    public Scheduling scheduling;

}

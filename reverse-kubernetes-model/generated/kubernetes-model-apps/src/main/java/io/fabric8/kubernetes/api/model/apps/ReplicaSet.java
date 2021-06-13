
package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class ReplicaSet implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apps/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ReplicaSet";
    public ObjectMeta metadata;
    public ReplicaSetSpec spec;
    public ReplicaSetStatus status;

}

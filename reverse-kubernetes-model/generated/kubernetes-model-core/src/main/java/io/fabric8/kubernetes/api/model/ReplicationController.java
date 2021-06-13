
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ReplicationController implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ReplicationController";
    public ObjectMeta metadata;
    public ReplicationControllerSpec spec;
    public ReplicationControllerStatus status;

}

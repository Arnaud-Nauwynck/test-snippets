
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Node implements HasMetadata
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
    public String kind = "Node";
    public ObjectMeta metadata;
    public NodeSpec spec;
    public NodeStatus status;

}

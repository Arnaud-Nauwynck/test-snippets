
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Namespace implements HasMetadata
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
    public String kind = "Namespace";
    public ObjectMeta metadata;
    public NamespaceSpec spec;
    public NamespaceStatus status;

}

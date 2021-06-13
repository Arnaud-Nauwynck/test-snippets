
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ResourceQuota implements HasMetadata, Namespaced
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
    public String kind = "ResourceQuota";
    public ObjectMeta metadata;
    public ResourceQuotaSpec spec;
    public ResourceQuotaStatus status;

}

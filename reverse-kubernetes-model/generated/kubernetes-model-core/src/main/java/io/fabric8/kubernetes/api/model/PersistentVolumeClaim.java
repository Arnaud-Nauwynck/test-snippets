
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolumeClaim implements HasMetadata, Namespaced
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
    public String kind = "PersistentVolumeClaim";
    public ObjectMeta metadata;
    public PersistentVolumeClaimSpec spec;
    public PersistentVolumeClaimStatus status;

}

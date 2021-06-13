
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolume implements HasMetadata
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
    public String kind = "PersistentVolume";
    public ObjectMeta metadata;
    public PersistentVolumeSpec spec;
    public PersistentVolumeStatus status;

}

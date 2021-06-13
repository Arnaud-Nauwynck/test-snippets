
package io.fabric8.kubernetes.api.model.storage;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class VolumeAttachment implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "storage.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "VolumeAttachment";
    public ObjectMeta metadata;
    public VolumeAttachmentSpec spec;
    public VolumeAttachmentStatus status;

}

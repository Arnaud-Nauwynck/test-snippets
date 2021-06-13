
package io.fabric8.kubernetes.api.model.storage;

import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class VolumeAttachmentStatus implements KubernetesResource
{

    public VolumeError attachError;
    public boolean attached;
    public Map<String, String> attachmentMetadata;
    public VolumeError detachError;

}

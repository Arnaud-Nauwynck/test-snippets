
package io.fabric8.kubernetes.api.model.storage;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpec;

@Generated("jsonschema2pojo")
public class VolumeAttachmentSource implements KubernetesResource
{

    public PersistentVolumeSpec inlineVolumeSpec;
    public String persistentVolumeName;

}

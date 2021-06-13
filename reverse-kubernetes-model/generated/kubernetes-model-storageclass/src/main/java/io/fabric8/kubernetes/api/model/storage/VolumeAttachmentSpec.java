
package io.fabric8.kubernetes.api.model.storage;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class VolumeAttachmentSpec implements KubernetesResource
{

    public String attacher;
    public String nodeName;
    public VolumeAttachmentSource source;

}

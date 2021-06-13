
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CSIVolumeSource implements KubernetesResource
{

    public java.lang.String driver;
    public java.lang.String fsType;
    public LocalObjectReference nodePublishSecretRef;
    public boolean readOnly;
    public Map<String, String> volumeAttributes;

}

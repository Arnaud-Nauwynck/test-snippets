
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CSIPersistentVolumeSource implements KubernetesResource
{

    public SecretReference controllerExpandSecretRef;
    public SecretReference controllerPublishSecretRef;
    public java.lang.String driver;
    public java.lang.String fsType;
    public SecretReference nodePublishSecretRef;
    public SecretReference nodeStageSecretRef;
    public boolean readOnly;
    public Map<String, String> volumeAttributes;
    public java.lang.String volumeHandle;

}

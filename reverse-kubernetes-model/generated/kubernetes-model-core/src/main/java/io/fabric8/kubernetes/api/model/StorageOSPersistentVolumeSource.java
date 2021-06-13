
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class StorageOSPersistentVolumeSource implements KubernetesResource
{

    public String fsType;
    public boolean readOnly;
    public ObjectReference secretRef;
    public String volumeName;
    public String volumeNamespace;

}

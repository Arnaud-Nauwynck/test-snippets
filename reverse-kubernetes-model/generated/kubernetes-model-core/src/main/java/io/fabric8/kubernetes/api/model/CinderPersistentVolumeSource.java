
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CinderPersistentVolumeSource implements KubernetesResource
{

    public String fsType;
    public boolean readOnly;
    public SecretReference secretRef;
    public String volumeID;

}

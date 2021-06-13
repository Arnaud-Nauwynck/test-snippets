
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class CinderVolumeSource implements KubernetesResource
{

    public String fsType;
    public boolean readOnly;
    public LocalObjectReference secretRef;
    public String volumeID;

}

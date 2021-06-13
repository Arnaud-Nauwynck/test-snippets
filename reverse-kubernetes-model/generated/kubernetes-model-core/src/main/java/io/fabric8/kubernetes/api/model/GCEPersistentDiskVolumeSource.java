
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class GCEPersistentDiskVolumeSource implements KubernetesResource
{

    public String fsType;
    public int partition;
    public String pdName;
    public boolean readOnly;

}

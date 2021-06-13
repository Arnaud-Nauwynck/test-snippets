
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolumeClaimSpec implements KubernetesResource
{

    public List<String> accessModes = new ArrayList<String>();
    public TypedLocalObjectReference dataSource;
    public ResourceRequirements resources;
    public LabelSelector selector;
    public String storageClassName;
    public String volumeMode;
    public String volumeName;

}

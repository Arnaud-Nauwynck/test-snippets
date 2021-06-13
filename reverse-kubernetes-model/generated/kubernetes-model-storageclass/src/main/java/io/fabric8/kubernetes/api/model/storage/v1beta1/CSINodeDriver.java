
package io.fabric8.kubernetes.api.model.storage.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CSINodeDriver implements KubernetesResource
{

    public VolumeNodeResources allocatable;
    public String name;
    public String nodeID;
    public List<String> topologyKeys = new ArrayList<String>();

}


package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolumeList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.PersistentVolume>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.PersistentVolume> items = new ArrayList<io.fabric8.kubernetes.api.model.PersistentVolume>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PersistentVolumeList";
    public ListMeta metadata;

}

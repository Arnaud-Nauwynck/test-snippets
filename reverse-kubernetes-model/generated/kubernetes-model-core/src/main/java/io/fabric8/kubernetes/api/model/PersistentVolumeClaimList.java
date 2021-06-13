
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolumeClaimList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.PersistentVolumeClaim>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.PersistentVolumeClaim> items = new ArrayList<io.fabric8.kubernetes.api.model.PersistentVolumeClaim>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PersistentVolumeClaimList";
    public ListMeta metadata;

}

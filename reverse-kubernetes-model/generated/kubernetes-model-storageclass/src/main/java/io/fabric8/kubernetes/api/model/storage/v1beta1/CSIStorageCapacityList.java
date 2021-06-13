
package io.fabric8.kubernetes.api.model.storage.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class CSIStorageCapacityList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.storage.v1beta1.CSIStorageCapacity>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "storage.k8s.io/v1beta1";
    public List<io.fabric8.kubernetes.api.model.storage.v1beta1.CSIStorageCapacity> items = new ArrayList<io.fabric8.kubernetes.api.model.storage.v1beta1.CSIStorageCapacity>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CSIStorageCapacityList";
    public ListMeta metadata;

}

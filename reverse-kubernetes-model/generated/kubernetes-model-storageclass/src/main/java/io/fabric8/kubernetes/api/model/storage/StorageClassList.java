
package io.fabric8.kubernetes.api.model.storage;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class StorageClassList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.storage.StorageClass>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "storage.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.storage.StorageClass> items = new ArrayList<io.fabric8.kubernetes.api.model.storage.StorageClass>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "StorageClassList";
    public ListMeta metadata;

}

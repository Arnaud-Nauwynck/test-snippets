
package io.fabric8.kubernetes.api.model.storage;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class CSIDriverList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.storage.CSIDriver>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "storage.k8s.io/v1";
    public List<io.fabric8.kubernetes.api.model.storage.CSIDriver> items = new ArrayList<io.fabric8.kubernetes.api.model.storage.CSIDriver>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CSIDriverList";
    public ListMeta metadata;

}

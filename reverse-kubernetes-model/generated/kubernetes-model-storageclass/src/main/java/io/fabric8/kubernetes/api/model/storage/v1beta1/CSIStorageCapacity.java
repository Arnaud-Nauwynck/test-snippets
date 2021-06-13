
package io.fabric8.kubernetes.api.model.storage.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class CSIStorageCapacity implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "storage.k8s.io/v1beta1";
    public Quantity capacity;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CSIStorageCapacity";
    public Quantity maximumVolumeSize;
    public ObjectMeta metadata;
    public LabelSelector nodeTopology;
    public String storageClassName;

}

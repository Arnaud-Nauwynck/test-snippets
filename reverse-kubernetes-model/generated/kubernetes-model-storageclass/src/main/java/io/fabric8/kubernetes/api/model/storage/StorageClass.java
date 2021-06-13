
package io.fabric8.kubernetes.api.model.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.TopologySelectorTerm;

@Generated("jsonschema2pojo")
public class StorageClass implements HasMetadata
{

    public boolean allowVolumeExpansion;
    public List<TopologySelectorTerm> allowedTopologies = new ArrayList<TopologySelectorTerm>();
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "storage.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "StorageClass";
    public ObjectMeta metadata;
    public List<java.lang.String> mountOptions = new ArrayList<java.lang.String>();
    public Map<String, String> parameters;
    public java.lang.String provisioner;
    public java.lang.String reclaimPolicy;
    public java.lang.String volumeBindingMode;

}

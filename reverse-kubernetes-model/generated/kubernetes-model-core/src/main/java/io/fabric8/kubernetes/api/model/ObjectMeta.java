
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ObjectMeta implements KubernetesResource
{

    public Map<String, String> annotations;
    public java.lang.String clusterName;
    public String creationTimestamp;
    public Long deletionGracePeriodSeconds;
    public String deletionTimestamp;
    public List<java.lang.String> finalizers = new ArrayList<java.lang.String>();
    public java.lang.String generateName;
    public Long generation;
    public Map<String, String> labels;
    public List<ManagedFieldsEntry> managedFields = new ArrayList<ManagedFieldsEntry>();
    public java.lang.String name;
    public java.lang.String namespace;
    public List<OwnerReference> ownerReferences = new ArrayList<OwnerReference>();
    public java.lang.String resourceVersion;
    public java.lang.String selfLink;
    public java.lang.String uid;

}

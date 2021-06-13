
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ServiceAccount implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public boolean automountServiceAccountToken;
    public List<LocalObjectReference> imagePullSecrets = new ArrayList<LocalObjectReference>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ServiceAccount";
    public ObjectMeta metadata;
    public List<ObjectReference> secrets = new ArrayList<ObjectReference>();

}

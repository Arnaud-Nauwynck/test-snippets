
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class Deployment implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "extensions/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Deployment";
    public ObjectMeta metadata;
    public DeploymentSpec spec;
    public DeploymentStatus status;

}

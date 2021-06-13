
package io.fabric8.kubernetes.api.model.extensions;

import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class DeploymentRollback implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "extensions/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "DeploymentRollback";
    public java.lang.String name;
    public RollbackConfig rollbackTo;
    public Map<String, String> updatedAnnotations;

}

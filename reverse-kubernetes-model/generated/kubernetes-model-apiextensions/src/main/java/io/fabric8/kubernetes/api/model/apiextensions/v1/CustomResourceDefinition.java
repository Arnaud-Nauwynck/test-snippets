
package io.fabric8.kubernetes.api.model.apiextensions.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class CustomResourceDefinition implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apiextensions.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CustomResourceDefinition";
    public ObjectMeta metadata;
    public CustomResourceDefinitionSpec spec;
    public CustomResourceDefinitionStatus status;

}

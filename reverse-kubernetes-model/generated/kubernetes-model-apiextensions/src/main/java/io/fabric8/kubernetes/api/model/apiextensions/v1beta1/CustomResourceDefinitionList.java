
package io.fabric8.kubernetes.api.model.apiextensions.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class CustomResourceDefinitionList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apiextensions.k8s.io/v1beta1";
    public List<io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition> items = new ArrayList<io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CustomResourceDefinitionList";
    public ListMeta metadata;

}


package io.fabric8.kubernetes.api.model.apiextensions.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CustomResourceDefinitionVersion implements KubernetesResource
{

    public List<CustomResourceColumnDefinition> additionalPrinterColumns = new ArrayList<CustomResourceColumnDefinition>();
    public boolean deprecated;
    public String deprecationWarning;
    public String name;
    public CustomResourceValidation schema;
    public boolean served;
    public boolean storage;
    public CustomResourceSubresources subresources;

}

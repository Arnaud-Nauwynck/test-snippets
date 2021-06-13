
package io.fabric8.kubernetes.api.model.apiextensions.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CustomResourceDefinitionSpec implements KubernetesResource
{

    public CustomResourceConversion conversion;
    public String group;
    public CustomResourceDefinitionNames names;
    public boolean preserveUnknownFields;
    public String scope;
    public List<CustomResourceDefinitionVersion> versions = new ArrayList<CustomResourceDefinitionVersion>();

}

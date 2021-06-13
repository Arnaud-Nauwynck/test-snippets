
package io.fabric8.kubernetes.api.model.apiextensions.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CustomResourceDefinitionStatus implements KubernetesResource
{

    public CustomResourceDefinitionNames acceptedNames;
    public List<CustomResourceDefinitionCondition> conditions = new ArrayList<CustomResourceDefinitionCondition>();
    public List<String> storedVersions = new ArrayList<String>();

}

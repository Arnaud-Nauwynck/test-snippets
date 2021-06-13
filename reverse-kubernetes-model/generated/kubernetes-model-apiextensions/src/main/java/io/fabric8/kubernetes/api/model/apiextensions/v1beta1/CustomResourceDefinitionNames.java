
package io.fabric8.kubernetes.api.model.apiextensions.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CustomResourceDefinitionNames implements KubernetesResource
{

    public List<String> categories = new ArrayList<String>();
    public String kind;
    public String listKind;
    public String plural;
    public List<String> shortNames = new ArrayList<String>();
    public String singular;

}

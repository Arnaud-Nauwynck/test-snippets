
package io.fabric8.kubernetes.api.model.admissionregistration.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class Rule implements KubernetesResource
{

    public List<String> apiGroups = new ArrayList<String>();
    public List<String> apiVersions = new ArrayList<String>();
    public List<String> resources = new ArrayList<String>();
    public String scope;

}

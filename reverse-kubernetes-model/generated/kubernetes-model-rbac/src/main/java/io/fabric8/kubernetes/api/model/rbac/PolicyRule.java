
package io.fabric8.kubernetes.api.model.rbac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class PolicyRule implements KubernetesResource
{

    public List<String> apiGroups = new ArrayList<String>();
    public List<String> nonResourceURLs = new ArrayList<String>();
    public List<String> resourceNames = new ArrayList<String>();
    public List<String> resources = new ArrayList<String>();
    public List<String> verbs = new ArrayList<String>();

}

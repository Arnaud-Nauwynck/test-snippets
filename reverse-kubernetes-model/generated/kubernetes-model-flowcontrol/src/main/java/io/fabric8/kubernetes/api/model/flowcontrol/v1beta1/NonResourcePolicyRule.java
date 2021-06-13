
package io.fabric8.kubernetes.api.model.flowcontrol.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class NonResourcePolicyRule implements KubernetesResource
{

    public List<String> nonResourceURLs = new ArrayList<String>();
    public List<String> verbs = new ArrayList<String>();

}

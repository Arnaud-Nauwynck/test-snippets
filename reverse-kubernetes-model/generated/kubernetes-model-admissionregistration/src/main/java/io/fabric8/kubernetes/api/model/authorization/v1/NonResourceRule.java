
package io.fabric8.kubernetes.api.model.authorization.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class NonResourceRule implements KubernetesResource
{

    public List<String> nonResourceURLs = new ArrayList<String>();
    public List<String> verbs = new ArrayList<String>();

}

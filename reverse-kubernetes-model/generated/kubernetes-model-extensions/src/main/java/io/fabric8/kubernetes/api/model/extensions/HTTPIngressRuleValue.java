
package io.fabric8.kubernetes.api.model.extensions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class HTTPIngressRuleValue implements KubernetesResource
{

    public List<HTTPIngressPath> paths = new ArrayList<HTTPIngressPath>();

}


package io.fabric8.kubernetes.api.model.node.v1beta1;

import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Quantity;

@Generated("jsonschema2pojo")
public class Overhead implements KubernetesResource
{

    public Map<String, Quantity> podFixed;

}

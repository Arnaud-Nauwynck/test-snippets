
package io.fabric8.kubernetes.api.model.node.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.Toleration;

@Generated("jsonschema2pojo")
public class Scheduling implements KubernetesResource
{

    public Map<String, String> nodeSelector;
    public List<Toleration> tolerations = new ArrayList<Toleration>();

}

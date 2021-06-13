
package io.fabric8.kubernetes.api.model.flowcontrol.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class PriorityLevelConfigurationList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.flowcontrol.v1beta1.PriorityLevelConfiguration>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "flowcontrol.apiserver.k8s.io/v1beta1";
    public List<io.fabric8.kubernetes.api.model.flowcontrol.v1beta1.PriorityLevelConfiguration> items = new ArrayList<io.fabric8.kubernetes.api.model.flowcontrol.v1beta1.PriorityLevelConfiguration>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PriorityLevelConfigurationList";
    public ListMeta metadata;

}

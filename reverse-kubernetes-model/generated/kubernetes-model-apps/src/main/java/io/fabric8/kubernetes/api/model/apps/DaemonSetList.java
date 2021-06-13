
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class DaemonSetList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.apps.DaemonSet>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apps/v1";
    public List<io.fabric8.kubernetes.api.model.apps.DaemonSet> items = new ArrayList<io.fabric8.kubernetes.api.model.apps.DaemonSet>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "DaemonSetList";
    public ListMeta metadata;

}

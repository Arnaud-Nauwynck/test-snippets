
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class ControllerRevisionList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.apps.ControllerRevision>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apps/v1";
    public List<io.fabric8.kubernetes.api.model.apps.ControllerRevision> items = new ArrayList<io.fabric8.kubernetes.api.model.apps.ControllerRevision>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ControllerRevisionList";
    public ListMeta metadata;

}


package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LimitRangeList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.LimitRange>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<io.fabric8.kubernetes.api.model.LimitRange> items = new ArrayList<io.fabric8.kubernetes.api.model.LimitRange>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "LimitRangeList";
    public ListMeta metadata;

}

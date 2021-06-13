
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class APIGroupList implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<APIGroup> groups = new ArrayList<APIGroup>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "APIGroupList";

}

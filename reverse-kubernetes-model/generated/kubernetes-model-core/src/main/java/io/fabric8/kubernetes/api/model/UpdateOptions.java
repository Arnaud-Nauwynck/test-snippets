
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class UpdateOptions implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<String> dryRun = new ArrayList<String>();
    public String fieldManager;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "UpdateOptions";

}

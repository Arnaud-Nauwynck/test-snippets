
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class DeleteOptions implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<String> dryRun = new ArrayList<String>();
    public Long gracePeriodSeconds;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "DeleteOptions";
    public boolean orphanDependents;
    public Preconditions preconditions;
    public String propagationPolicy;

}

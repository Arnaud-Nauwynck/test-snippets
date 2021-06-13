
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PodExecOptions implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<String> command = new ArrayList<String>();
    public String container;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "PodExecOptions";
    public boolean stderr;
    public boolean stdin;
    public boolean stdout;
    public boolean tty;

}

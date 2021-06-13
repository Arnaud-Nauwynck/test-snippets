
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Status implements KubernetesResource
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public int code;
    public StatusDetails details;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Status";
    public String message;
    public ListMeta metadata;
    public String reason;
    public String status;

}


package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ListOptions implements KubernetesResource
{

    public boolean allowWatchBookmarks;
    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public String _continue;
    public String fieldSelector;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ListOptions";
    public String labelSelector;
    public Long limit;
    public String resourceVersion;
    public String resourceVersionMatch;
    public Long timeoutSeconds;
    public boolean watch;

}

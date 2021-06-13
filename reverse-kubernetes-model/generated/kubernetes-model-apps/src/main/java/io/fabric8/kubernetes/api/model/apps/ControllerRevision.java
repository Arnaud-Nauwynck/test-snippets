
package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class ControllerRevision implements io.fabric8.kubernetes.api.model.HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apps/v1";
    public io.fabric8.kubernetes.api.model.HasMetadata data;
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ControllerRevision";
    public ObjectMeta metadata;
    public Long revision;

}

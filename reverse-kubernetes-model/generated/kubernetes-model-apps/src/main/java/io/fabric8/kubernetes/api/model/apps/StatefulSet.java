
package io.fabric8.kubernetes.api.model.apps;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class StatefulSet implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "apps/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "StatefulSet";
    public ObjectMeta metadata;
    public StatefulSetSpec spec;
    public StatefulSetStatus status;

}

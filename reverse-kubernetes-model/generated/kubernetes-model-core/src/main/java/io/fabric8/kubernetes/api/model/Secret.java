
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Secret implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "v1";
    public Map<String, String> data;
    public boolean immutable;
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "Secret";
    public ObjectMeta metadata;
    public Map<String, String> stringData;
    public java.lang.String type;

}

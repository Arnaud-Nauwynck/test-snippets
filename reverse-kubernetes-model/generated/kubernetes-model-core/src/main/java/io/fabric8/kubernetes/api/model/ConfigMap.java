
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ConfigMap implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String apiVersion = "v1";
    public Map<String, String> binaryData;
    public Map<String, String> data;
    public boolean immutable;
    /**
     * 
     * (Required)
     * 
     */
    public java.lang.String kind = "ConfigMap";
    public ObjectMeta metadata;

}

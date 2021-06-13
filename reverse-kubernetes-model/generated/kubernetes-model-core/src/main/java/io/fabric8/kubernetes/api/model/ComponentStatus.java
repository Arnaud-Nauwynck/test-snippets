
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ComponentStatus implements HasMetadata
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "v1";
    public List<ComponentCondition> conditions = new ArrayList<ComponentCondition>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "ComponentStatus";
    public ObjectMeta metadata;

}

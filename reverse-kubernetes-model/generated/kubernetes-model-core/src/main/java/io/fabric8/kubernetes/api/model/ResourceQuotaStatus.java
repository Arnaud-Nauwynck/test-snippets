
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ResourceQuotaStatus implements KubernetesResource
{

    public Map<String, io.fabric8.kubernetes.api.model.Quantity> hard;
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> used;

}

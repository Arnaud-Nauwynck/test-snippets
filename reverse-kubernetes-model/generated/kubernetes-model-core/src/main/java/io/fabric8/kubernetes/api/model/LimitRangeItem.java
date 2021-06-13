
package io.fabric8.kubernetes.api.model;

import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LimitRangeItem implements KubernetesResource
{

    public Map<String, io.fabric8.kubernetes.api.model.Quantity> _default;
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> defaultRequest;
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> max;
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> maxLimitRequestRatio;
    public Map<String, io.fabric8.kubernetes.api.model.Quantity> min;
    public java.lang.String type;

}

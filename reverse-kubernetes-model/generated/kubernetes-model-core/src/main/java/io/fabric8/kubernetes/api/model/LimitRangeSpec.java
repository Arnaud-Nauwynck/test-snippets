
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class LimitRangeSpec implements KubernetesResource
{

    public List<LimitRangeItem> limits = new ArrayList<LimitRangeItem>();

}

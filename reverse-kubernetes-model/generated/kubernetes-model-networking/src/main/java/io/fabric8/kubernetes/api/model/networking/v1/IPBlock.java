
package io.fabric8.kubernetes.api.model.networking.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class IPBlock implements KubernetesResource
{

    public String cidr;
    public List<String> except = new ArrayList<String>();

}

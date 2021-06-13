
package io.fabric8.kubernetes.api.model.networking.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class IngressTLS implements KubernetesResource
{

    public List<String> hosts = new ArrayList<String>();
    public String secretName;

}

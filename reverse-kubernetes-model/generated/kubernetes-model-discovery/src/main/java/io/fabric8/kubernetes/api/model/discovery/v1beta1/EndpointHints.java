
package io.fabric8.kubernetes.api.model.discovery.v1beta1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class EndpointHints implements KubernetesResource
{

    public List<ForZone> forZones = new ArrayList<ForZone>();

}


package io.fabric8.kubernetes.api.model.extensions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class NetworkPolicyEgressRule implements KubernetesResource
{

    public List<NetworkPolicyPort> ports = new ArrayList<NetworkPolicyPort>();
    public List<NetworkPolicyPeer> to = new ArrayList<NetworkPolicyPeer>();

}

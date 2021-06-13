
package io.fabric8.kubernetes.api.model.networking.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;

@Generated("jsonschema2pojo")
public class NetworkPolicyPeer implements KubernetesResource
{

    public IPBlock ipBlock;
    public LabelSelector namespaceSelector;
    public LabelSelector podSelector;

}

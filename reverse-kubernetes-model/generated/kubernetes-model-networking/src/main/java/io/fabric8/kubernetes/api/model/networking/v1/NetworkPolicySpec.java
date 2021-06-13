
package io.fabric8.kubernetes.api.model.networking.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;

@Generated("jsonschema2pojo")
public class NetworkPolicySpec implements KubernetesResource
{

    public List<NetworkPolicyEgressRule> egress = new ArrayList<NetworkPolicyEgressRule>();
    public List<NetworkPolicyIngressRule> ingress = new ArrayList<NetworkPolicyIngressRule>();
    public LabelSelector podSelector;
    public List<String> policyTypes = new ArrayList<String>();

}

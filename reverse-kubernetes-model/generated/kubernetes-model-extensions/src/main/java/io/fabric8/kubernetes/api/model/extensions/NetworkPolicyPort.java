
package io.fabric8.kubernetes.api.model.extensions;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class NetworkPolicyPort implements KubernetesResource
{

    public int endPort;
    public IntOrString port;
    public String protocol;

}

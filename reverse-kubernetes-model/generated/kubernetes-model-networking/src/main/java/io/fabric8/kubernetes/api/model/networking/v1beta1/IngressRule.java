
package io.fabric8.kubernetes.api.model.networking.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class IngressRule implements KubernetesResource
{

    public String host;
    public HTTPIngressRuleValue http;

}

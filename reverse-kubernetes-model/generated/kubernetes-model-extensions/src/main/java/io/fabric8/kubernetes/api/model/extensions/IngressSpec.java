
package io.fabric8.kubernetes.api.model.extensions;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class IngressSpec implements KubernetesResource
{

    public IngressBackend backend;
    public String ingressClassName;
    public List<IngressRule> rules = new ArrayList<IngressRule>();
    public List<IngressTLS> tls = new ArrayList<IngressTLS>();

}

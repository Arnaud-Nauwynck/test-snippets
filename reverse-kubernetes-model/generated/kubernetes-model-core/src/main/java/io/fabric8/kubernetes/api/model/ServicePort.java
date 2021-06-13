
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ServicePort implements KubernetesResource
{

    public String appProtocol;
    public String name;
    public int nodePort;
    public int port;
    public String protocol;
    public IntOrString targetPort;

}

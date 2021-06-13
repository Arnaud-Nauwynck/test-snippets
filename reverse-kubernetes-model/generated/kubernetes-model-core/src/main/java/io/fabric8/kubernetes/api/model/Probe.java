
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Probe implements KubernetesResource
{

    public ExecAction exec;
    public int failureThreshold;
    public HTTPGetAction httpGet;
    public int initialDelaySeconds;
    public int periodSeconds;
    public int successThreshold;
    public TCPSocketAction tcpSocket;
    public Long terminationGracePeriodSeconds;
    public int timeoutSeconds;

}

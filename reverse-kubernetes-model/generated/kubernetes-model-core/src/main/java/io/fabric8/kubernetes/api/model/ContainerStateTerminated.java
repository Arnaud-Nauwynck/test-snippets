
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ContainerStateTerminated implements KubernetesResource
{

    public java.lang.String containerID;
    public int exitCode;
    public String finishedAt;
    public java.lang.String message;
    public java.lang.String reason;
    public int signal;
    public String startedAt;

}

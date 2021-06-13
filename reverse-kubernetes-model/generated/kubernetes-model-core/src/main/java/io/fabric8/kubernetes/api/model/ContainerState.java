
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ContainerState implements KubernetesResource
{

    public ContainerStateRunning running;
    public ContainerStateTerminated terminated;
    public ContainerStateWaiting waiting;

}

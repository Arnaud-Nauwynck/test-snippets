
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ContainerStatus implements KubernetesResource
{

    public String containerID;
    public String image;
    public String imageID;
    public ContainerState lastState;
    public String name;
    public boolean ready;
    public int restartCount;
    public boolean started;
    public ContainerState state;

}

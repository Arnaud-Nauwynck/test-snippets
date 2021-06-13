
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class DownwardAPIVolumeFile implements KubernetesResource
{

    public ObjectFieldSelector fieldRef;
    public int mode;
    public String path;
    public ResourceFieldSelector resourceFieldRef;

}

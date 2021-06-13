
package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class VolumeMount implements KubernetesResource
{

    public String mountPath;
    public String mountPropagation;
    public String name;
    public boolean readOnly;
    public String subPath;
    public String subPathExpr;

}


package io.fabric8.kubernetes.api.model;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class OwnerReference implements KubernetesResource
{

    public String apiVersion;
    public boolean blockOwnerDeletion;
    public boolean controller;
    public String kind;
    public String name;
    public String uid;

}

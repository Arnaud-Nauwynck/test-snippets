
package io.fabric8.kubernetes.api.model.rbac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class RoleBinding implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "rbac.authorization.k8s.io/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "RoleBinding";
    public ObjectMeta metadata;
    public RoleRef roleRef;
    public List<Subject> subjects = new ArrayList<Subject>();

}

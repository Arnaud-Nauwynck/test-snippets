
package io.fabric8.kubernetes.api.model.authorization.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class SubjectAccessReviewSpec implements KubernetesResource
{

    public Map<String, ArrayList<String>> extra;
    public List<java.lang.String> groups = new ArrayList<java.lang.String>();
    public NonResourceAttributes nonResourceAttributes;
    public ResourceAttributes resourceAttributes;
    public java.lang.String uid;
    public java.lang.String user;

}


package io.fabric8.kubernetes.api.model.authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class UserInfo implements KubernetesResource
{

    public Map<String, ArrayList<String>> extra;
    public List<java.lang.String> groups = new ArrayList<java.lang.String>();
    public java.lang.String uid;
    public java.lang.String username;

}

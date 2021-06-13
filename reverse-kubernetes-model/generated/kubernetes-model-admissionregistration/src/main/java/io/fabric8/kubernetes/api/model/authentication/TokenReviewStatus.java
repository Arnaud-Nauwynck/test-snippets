
package io.fabric8.kubernetes.api.model.authentication;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class TokenReviewStatus implements KubernetesResource
{

    public List<String> audiences = new ArrayList<String>();
    public boolean authenticated;
    public String error;
    public UserInfo user;

}

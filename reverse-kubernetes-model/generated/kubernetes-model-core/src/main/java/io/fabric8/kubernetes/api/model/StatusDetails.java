
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class StatusDetails implements KubernetesResource
{

    public List<StatusCause> causes = new ArrayList<StatusCause>();
    public String group;
    public String kind;
    public String name;
    public int retryAfterSeconds;
    public String uid;

}

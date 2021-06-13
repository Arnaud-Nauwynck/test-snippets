
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class HostAlias implements KubernetesResource
{

    public List<String> hostnames = new ArrayList<String>();
    public String ip;

}

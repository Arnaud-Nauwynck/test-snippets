
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Context implements KubernetesResource
{

    public String cluster;
    public List<NamedExtension> extensions = new ArrayList<NamedExtension>();
    public String namespace;
    public String user;

}

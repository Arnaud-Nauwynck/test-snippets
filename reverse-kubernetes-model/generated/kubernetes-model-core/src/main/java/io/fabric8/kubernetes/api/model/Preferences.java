
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Preferences implements KubernetesResource
{

    public boolean colors;
    public List<NamedExtension> extensions = new ArrayList<NamedExtension>();

}

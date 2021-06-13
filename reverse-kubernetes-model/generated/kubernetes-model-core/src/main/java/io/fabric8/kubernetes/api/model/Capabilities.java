
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Capabilities implements KubernetesResource
{

    public List<String> add = new ArrayList<String>();
    public List<String> drop = new ArrayList<String>();

}

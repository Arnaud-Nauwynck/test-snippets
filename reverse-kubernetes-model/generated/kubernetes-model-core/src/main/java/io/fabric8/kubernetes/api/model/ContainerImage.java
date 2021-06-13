
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class ContainerImage implements KubernetesResource
{

    public List<String> names = new ArrayList<String>();
    public Long sizeBytes;

}

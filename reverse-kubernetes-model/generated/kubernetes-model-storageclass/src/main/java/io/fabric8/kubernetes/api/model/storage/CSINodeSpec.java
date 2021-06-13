
package io.fabric8.kubernetes.api.model.storage;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CSINodeSpec implements KubernetesResource
{

    public List<CSINodeDriver> drivers = new ArrayList<CSINodeDriver>();

}

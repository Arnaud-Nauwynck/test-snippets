
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class SecretVolumeSource implements KubernetesResource
{

    public int defaultMode;
    public List<KeyToPath> items = new ArrayList<KeyToPath>();
    public boolean optional;
    public String secretName;

}

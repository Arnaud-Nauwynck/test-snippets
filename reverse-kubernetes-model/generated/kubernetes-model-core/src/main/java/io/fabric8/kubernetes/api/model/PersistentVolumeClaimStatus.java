
package io.fabric8.kubernetes.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class PersistentVolumeClaimStatus implements KubernetesResource
{

    public List<java.lang.String> accessModes = new ArrayList<java.lang.String>();
    public Map<String, Quantity> capacity;
    public List<PersistentVolumeClaimCondition> conditions = new ArrayList<PersistentVolumeClaimCondition>();
    public java.lang.String phase;

}

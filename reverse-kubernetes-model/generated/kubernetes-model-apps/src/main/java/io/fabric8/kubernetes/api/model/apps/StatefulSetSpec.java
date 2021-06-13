
package io.fabric8.kubernetes.api.model.apps;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.LabelSelector;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;

@Generated("jsonschema2pojo")
public class StatefulSetSpec implements KubernetesResource
{

    public String podManagementPolicy;
    public int replicas;
    public int revisionHistoryLimit;
    public LabelSelector selector;
    public String serviceName;
    public PodTemplateSpec template;
    public StatefulSetUpdateStrategy updateStrategy;
    public List<PersistentVolumeClaim> volumeClaimTemplates = new ArrayList<PersistentVolumeClaim>();

}

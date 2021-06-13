
package io.fabric8.kubernetes.api.model.coordination.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.MicroTime;

@Generated("jsonschema2pojo")
public class LeaseSpec implements KubernetesResource
{

    public MicroTime acquireTime;
    public String holderIdentity;
    public int leaseDurationSeconds;
    public int leaseTransitions;
    public MicroTime renewTime;

}

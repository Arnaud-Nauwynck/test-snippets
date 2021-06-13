
package io.fabric8.kubernetes.api.model.storage;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CSIDriverSpec implements KubernetesResource
{

    public boolean attachRequired;
    public String fsGroupPolicy;
    public boolean podInfoOnMount;
    public boolean requiresRepublish;
    public boolean storageCapacity;
    public List<TokenRequest> tokenRequests = new ArrayList<TokenRequest>();
    public List<String> volumeLifecycleModes = new ArrayList<String>();

}

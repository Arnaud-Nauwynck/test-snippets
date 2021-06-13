
package io.fabric8.kubernetes.api.model.batch.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectReference;

@Generated("jsonschema2pojo")
public class CronJobStatus implements KubernetesResource
{

    public List<ObjectReference> active = new ArrayList<ObjectReference>();
    public String lastScheduleTime;
    public String lastSuccessfulTime;

}

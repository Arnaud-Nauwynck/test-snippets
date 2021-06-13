
package io.fabric8.kubernetes.api.model.batch.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class JobStatus implements KubernetesResource
{

    public int active;
    public java.lang.String completedIndexes;
    public String completionTime;
    public List<JobCondition> conditions = new ArrayList<JobCondition>();
    public int failed;
    public String startTime;
    public int succeeded;

}

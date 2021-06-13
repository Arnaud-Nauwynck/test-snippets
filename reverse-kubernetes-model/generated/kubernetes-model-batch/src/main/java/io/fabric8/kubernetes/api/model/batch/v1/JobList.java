
package io.fabric8.kubernetes.api.model.batch.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class JobList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.batch.v1.Job>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "batch/v1";
    public List<io.fabric8.kubernetes.api.model.batch.v1.Job> items = new ArrayList<io.fabric8.kubernetes.api.model.batch.v1.Job>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "JobList";
    public ListMeta metadata;

}


package io.fabric8.kubernetes.api.model.batch.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ListMeta;

@Generated("jsonschema2pojo")
public class CronJobList implements KubernetesResource, KubernetesResourceList<io.fabric8.kubernetes.api.model.batch.v1.CronJob>
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "batch/v1";
    public List<io.fabric8.kubernetes.api.model.batch.v1.CronJob> items = new ArrayList<io.fabric8.kubernetes.api.model.batch.v1.CronJob>();
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CronJobList";
    public ListMeta metadata;

}

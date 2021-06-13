
package io.fabric8.kubernetes.api.model.batch.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.KubernetesResource;

@Generated("jsonschema2pojo")
public class CronJobSpec implements KubernetesResource
{

    public String concurrencyPolicy;
    public int failedJobsHistoryLimit;
    public JobTemplateSpec jobTemplate;
    public String schedule;
    public Long startingDeadlineSeconds;
    public int successfulJobsHistoryLimit;
    public boolean suspend;

}

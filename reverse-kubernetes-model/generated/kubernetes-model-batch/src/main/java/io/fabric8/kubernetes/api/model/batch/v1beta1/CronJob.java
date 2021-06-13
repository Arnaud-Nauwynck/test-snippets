
package io.fabric8.kubernetes.api.model.batch.v1beta1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class CronJob implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "batch/v1beta1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "CronJob";
    public ObjectMeta metadata;
    public CronJobSpec spec;
    public CronJobStatus status;

}

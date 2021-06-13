
package io.fabric8.kubernetes.api.model.batch.v1;

import javax.annotation.Generated;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.api.model.ObjectMeta;

@Generated("jsonschema2pojo")
public class Job implements HasMetadata, Namespaced
{

    /**
     * 
     * (Required)
     * 
     */
    public String apiVersion = "batch/v1";
    /**
     * 
     * (Required)
     * 
     */
    public String kind = "Job";
    public ObjectMeta metadata;
    public JobSpec spec;
    public JobStatus status;

}
